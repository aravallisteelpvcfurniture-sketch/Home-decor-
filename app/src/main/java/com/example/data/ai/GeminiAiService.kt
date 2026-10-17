package com.example.data.ai

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class AiToolType(val titleHindi: String, val titleEnglish: String) {
    POSTER_GENERATOR("पोस्टर एवं ऑफर जनरेटर", "AI Poster & Offer Generator"),
    PRODUCT_DESCRIPTION("उत्पाद विवरण लेखक", "Product Description Generator"),
    MARKETING_CAPTION("मार्केटिंग कैप्शन (हिंदी/गुजराती/EN)", "Multi-Language Captions"),
    WHATSAPP_PROMO("व्हाट्सएप प्रचार संदेश", "WhatsApp Promotional Messages"),
    FESTIVAL_GREETING("त्योहार शुभकामना संदेश", "Festival Greeting Generator"),
    FURNITURE_DESIGN_IDEAS("फर्नीचर डिज़ाइन और सुझाव", "Furniture Design Ideas"),
    CUSTOMER_REPLY_ASSISTANT("ग्राहक उत्तर सहायक (Customer Reply)", "Customer Reply Assistant"),
    CATALOGUE_TEXT("कैटलॉग विवरण निर्माता", "Product Catalogue Text")
}

data class AiResult(
    val text: String,
    val model: String = "gemini-3.5-flash",
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
)

class GeminiAiService(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Configurable backend endpoint for production
    var customBackendUrl: String = ""

    suspend fun generateContent(
        toolType: AiToolType,
        userPrompt: String,
        selectedLanguage: String = "Hindi",
        businessLocation: String = "Shyam Arcade, Meghraj Road, Modasa, Gujarat 383315"
    ): AiResult = withContext(Dispatchers.IO) {
        val auth = Firebase.auth
        val currentUser = auth.currentUser

        // Build domain-specific prompt for Aravalli Furniture
        val systemPrompt = buildSystemPrompt(toolType, selectedLanguage, businessLocation)
        val fullPrompt = "$systemPrompt\n\nउपयोगकर्ता विवरण (User Request):\n$userPrompt"

        // 1. Production Path: If Backend URL is configured, use secure authenticated proxy
        if (customBackendUrl.isNotBlank()) {
            return@withContext callBackendProxy(fullPrompt, toolType, selectedLanguage)
        }

        // 2. Development / Secret Manager Path: Use Gemini REST with key from BuildConfig (.env)
        // Notice: The key is loaded at runtime via BuildConfig from local .env / AI Studio Secrets panel,
        // and is NEVER hardcoded in source files.
        val runtimeApiKey = getRuntimeApiKey()
        if (runtimeApiKey.isNotBlank()) {
            return@withContext callDirectGeminiRest(fullPrompt, runtimeApiKey)
        }

        // 3. Fallback mock-free error instructing user on configuration
        AiResult(
            text = "",
            isSuccess = false,
            errorMessage = "जेमिनी API कुंजी या बैकएंड URL कॉन्फ़िगर नहीं है। कृपया ऐप की 'AI सेटिंग्स' में जाकर अपना सुरक्षित बैकएंड URL दर्ज करें, या Google AI Studio से API कुंजी Secrets Panel में जोड़ें।"
        )
    }

    private suspend fun callBackendProxy(
        prompt: String,
        toolType: AiToolType,
        language: String
    ): AiResult {
        return try {
            val user = Firebase.auth.currentUser
            val idToken = user?.getIdToken(false)?.await()?.token ?: ""

            val jsonPayload = JSONObject().apply {
                put("prompt", prompt)
                put("toolType", toolType.name)
                put("language", language)
                put("targetModel", "gemini-3.5-flash")
            }

            val request = Request.Builder()
                .url(customBackendUrl)
                .addHeader("Authorization", "Bearer $idToken")
                .addHeader("Content-Type", "application/json")
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val json = JSONObject(responseBody)
                val text = json.optString("text", "कोई परिणाम प्राप्त नहीं हुआ।")
                val model = json.optString("model", "gemini-3.5-flash")
                AiResult(text = text, model = model, isSuccess = true)
            } else {
                val errJson = try { JSONObject(responseBody) } catch (_: Exception) { null }
                val errorMsg = errJson?.optString("error") ?: "सर्वर त्रुटि: कोड ${response.code}"
                AiResult(text = "", isSuccess = false, errorMessage = errorMsg)
            }
        } catch (e: Exception) {
            Log.e("GeminiAiService", "Backend call failed: ${e.message}", e)
            AiResult(
                text = "",
                isSuccess = false,
                errorMessage = "बैकएंड से संपर्क करने में असमर्थ: ${e.localizedMessage ?: "नेटवर्क त्रुटि"}"
            )
        }
    }

    private fun callDirectGeminiRest(prompt: String, apiKey: String): AiResult {
        return try {
            val model = "gemini-3.5-flash"
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

            val contentsArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            }

            val requestBody = JSONObject().apply {
                put("contents", contentsArray)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.95)
                })
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val contentObj = firstCandidate?.optJSONObject("content")
                val parts = contentObj?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text") ?: "सामग्री उत्पन्न नहीं हो सकी।"

                AiResult(text = text, model = model, isSuccess = true)
            } else {
                val errJson = try { JSONObject(responseBody) } catch (_: Exception) { null }
                val errMsg = errJson?.optJSONObject("error")?.optString("message") 
                    ?: "API त्रुटि (${response.code})"
                AiResult(text = "", isSuccess = false, errorMessage = "जेमिनी त्रुटि: $errMsg")
            }
        } catch (e: Exception) {
            Log.e("GeminiAiService", "Direct call failed: ${e.message}", e)
            AiResult(
                text = "",
                isSuccess = false,
                errorMessage = "नेटवर्क कनेक्शन त्रुटि: ${e.localizedMessage ?: "कृपया इंटरनेट जांचें"}"
            )
        }
    }

    private fun getRuntimeApiKey(): String {
        return try {
            val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
            (field.get(null) as? String)?.trim() ?: ""
        } catch (_: Exception) {
            ""
        }
    }

    private fun buildSystemPrompt(
        toolType: AiToolType,
        language: String,
        location: String
    ): String {
        val baseContext = "You are the AI Business & Interior Design Assistant for 'ARAVALLI STEEL PVC FURNITURE' located at $location. " +
                "Primary categories: SS 304 Railing, Aluminium & 12mm Toughened Glass Balcony, 100% Waterproof PVC Modular Kitchens, UPVC Windows, Sliding Wardrobes, TV Units. " +
                "Language requested: $language (support authentic Hindi, Gujarati, or English as requested). "

        return when (toolType) {
            AiToolType.POSTER_GENERATOR -> 
                "$baseContext Create an eye-catching, professional promotional poster text with bold headlines, bulleted feature highlights, pricing hints, and a clear Call To Action (CTA) with phone number and address."
            AiToolType.PRODUCT_DESCRIPTION -> 
                "$baseContext Write a compelling, technical yet customer-friendly product catalog description. Highlight grade (e.g. SS 304 vs 202), durability, weather/termite resistance, finishes, and warranty."
            AiToolType.MARKETING_CAPTION -> 
                "$baseContext Generate 3 engaging, modern social media and WhatsApp status captions with relevant emojis and hashtags (e.g. #AravalliFurniture #Modasa #SS304Railing #ModularKitchen)."
            AiToolType.WHATSAPP_PROMO -> 
                "$baseContext Create a polite, concise, and persuasive WhatsApp broadcast message ready to share with prospective customers and architects. Include business contact info."
            AiToolType.FESTIVAL_GREETING -> 
                "$baseContext Write a warm and respectful festival greeting message connecting festive blessings with home improvement, new beginnings, and Aravalli Furniture's warm wishes."
            AiToolType.FURNITURE_DESIGN_IDEAS -> 
                "$baseContext Provide creative, practical interior design and fabrication suggestions. Suggest color combinations (e.g., acrylic gloss with charcoal louvers), layout ergonomics, and lighting ideas."
            AiToolType.CUSTOMER_REPLY_ASSISTANT -> 
                "$baseContext Act as an expert customer service representative for Aravalli Furniture. Provide a polite, reassuring, and helpful response addressing the customer's query regarding prices, measurements, quality guarantee, or site visits."
            AiToolType.CATALOGUE_TEXT -> 
                "$baseContext Create a structured, professional product catalog entry formatted with Title, Specifications, Material Grade, Recommended Uses, and Care Instructions."
        }
    }
}

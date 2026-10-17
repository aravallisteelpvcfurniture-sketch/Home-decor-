package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AiResult
import com.example.data.ai.AiToolType
import com.example.data.ai.GeminiAiService
import com.example.ui.components.openWhatsApp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.RedDanger
import com.example.ui.theme.SteelLight
import com.example.ui.viewmodel.AravalliViewModel
import kotlinx.coroutines.launch

@Composable
fun AiToolsScreen(viewModel: AravalliViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val aiService = remember { GeminiAiService(context) }
    val currentLang by viewModel.currentLanguage.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var selectedTool by remember { mutableStateOf(AiToolType.POSTER_GENERATOR) }
    var selectedLanguage by remember { mutableStateOf("Hindi") }
    var userPrompt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var aiResult by remember { mutableStateOf<AiResult?>(null) }
    var showSetupGuide by remember { mutableStateOf(false) }

    val quickPrompts = when (selectedTool) {
        AiToolType.POSTER_GENERATOR -> listOf(
            "दिवाली विशेष ऑफर: एसएस 304 रेलिंग और पीवीसी मॉड्यूलर किचन पर 10% छूट",
            "नए बंगले के लिए आधुनिक टफन्ड ग्लास बालकनी और खिड़कियों का प्रचार"
        )
        AiToolType.PRODUCT_DESCRIPTION -> listOf(
            "एसएस 304 ग्लास स्पिगोट बालकनी रेलिंग (12mm टफन्ड ग्लास के साथ)",
            "100% वाटरप्रूफ 18mm पीवीसी फोम शीट एल-शेप मॉड्यूलर किचन"
        )
        AiToolType.MARKETING_CAPTION -> listOf(
            "मोडासा में सबसे मजबूत स्टेनलेस स्टील रेलिंग का काम",
            "दीमक और पानी से 100% सुरक्षित आधुनिक पीवीसी अलमारी"
        )
        AiToolType.WHATSAPP_PROMO -> listOf(
            "मोडासा और आसपास के क्षेत्रों में फ्री साइट विजिट और माप कोटेशन",
            "त्योहार सीजन में नए घर की इंटीरियर फिटिंग्स पर विशेष बचत"
        )
        AiToolType.FESTIVAL_GREETING -> listOf(
            "शुभ दीपावली और नूतन वर्षाभिनंदन (Aravalli Steel Modasa)",
            "उतरायण / मकर संक्रांति की हार्दिक शुभकामनाएं"
        )
        AiToolType.FURNITURE_DESIGN_IDEAS -> listOf(
            "छोटे फ्लैट के लिए कॉम्पैक्ट एल-शेप पीवीसी किचन लेआउट सुझाव",
            "हॉल के लिए आधुनिक फ्लोटिंग टीवी यूनिट और चारकोल पैनल डिज़ाइन"
        )
        AiToolType.CUSTOMER_REPLY_ASSISTANT -> listOf(
            "ग्राहक पूछ रहा है: एसएस 304 और 202 रेलिंग में क्या अंतर है और क्या रेट है?",
            "ग्राहक पूछ रहा है: क्या पीवीसी किचन में पानी लगने से खराबी होगी?"
        )
        AiToolType.CATALOGUE_TEXT -> listOf(
            "3-ट्रैक यूपीवीसी स्लाइडिंग विंडो (मच्छर जाली के साथ) कैटलॉग एंट्री",
            "3-डोर स्लाइडिंग पीवीसी वॉर्डरोब (लॉकर और दराज के साथ)"
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_tools_screen")
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // AI Header Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "अरावल्ली जेमिनी AI टूल्स",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Google Gemini 3.5 Flash द्वारा संचालित",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldLight
                                )
                            }
                        }

                        Button(
                            onClick = { showSetupGuide = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NavySecondary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(14.dp), tint = GoldAccent)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("सुरक्षा गाइड", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Tool Selector Tabs
        item {
            Text(
                text = "AI टूल चुनें (Select AI Tool):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(AiToolType.values()) { tool ->
                    val isSelected = selectedTool == tool
                    Surface(
                        modifier = Modifier.clickable {
                            selectedTool = tool
                            userPrompt = ""
                        },
                        color = if (isSelected) NavyPrimary else SteelLight,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = tool.titleHindi,
                            color = if (isSelected) Color.White else Color.Black,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Language Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "आउटपुट भाषा (Output Language):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Hindi", "Gujarati", "English").forEach { lang ->
                        val isSel = selectedLanguage == lang
                        Surface(
                            modifier = Modifier.clickable { selectedLanguage = lang },
                            color = if (isSel) NavySecondary else SteelLight,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = when (lang) {
                                    "Hindi" -> "हिन्दी"
                                    "Gujarati" -> "ગુજરાતી"
                                    else -> "English"
                                },
                                color = if (isSel) Color.White else Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick Suggestion Prompts
        item {
            Text("त्वरित सुझाव (Quick Prompts):", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                quickPrompts.forEach { qp ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { userPrompt = qp },
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "💡 $qp",
                            style = MaterialTheme.typography.bodySmall,
                            color = NavySecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Input Prompt Field
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = userPrompt,
                        onValueChange = { userPrompt = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_prompt_input"),
                        label = { Text("प्रॉम्प्ट दर्ज करें (Describe your requirements...)") },
                        placeholder = { Text("उदा. 40 रनिंग फीट एसएस 304 रेलिंग का प्रचार संदेश बनाएं") },
                        maxLines = 5,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (userPrompt.isNotBlank() && !isLoading) {
                                isLoading = true
                                aiResult = null
                                coroutineScope.launch {
                                    val res = aiService.generateContent(
                                        toolType = selectedTool,
                                        userPrompt = userPrompt,
                                        selectedLanguage = selectedLanguage,
                                        businessLocation = settings?.address ?: "Shyam Arcade, Meghraj Road, Modasa"
                                    )
                                    aiResult = res
                                    isLoading = false
                                }
                            }
                        },
                        enabled = userPrompt.isNotBlank() && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("ai_generate_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("जेमिनी AI उत्पन्न कर रहा है...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("उत्पन्न करें (Generate with Gemini)")
                        }
                    }
                }
            }
        }

        // Result Card
        aiResult?.let { res ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_result_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (res.isSuccess) Color.White else Color(0xFFFEF2F2)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (res.isSuccess) "परिणाम (${res.model})" else "त्रुटि सूचना",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (res.isSuccess) NavyPrimary else RedDanger
                            )

                            if (res.isSuccess) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Button(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("AI Output", res.text))
                                            Toast.makeText(context, "कॉपी किया गया!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SteelLight),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = NavyPrimary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("कॉपी", fontSize = 11.sp, color = NavyPrimary)
                                    }

                                    Button(
                                        onClick = {
                                            openWhatsApp(context, "", res.text)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("व्हाट्सएप", fontSize = 11.sp, color = Color.White)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = SteelLight)
                        Spacer(modifier = Modifier.height(10.dp))

                        if (res.isSuccess) {
                            Text(
                                text = res.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF0F172A),
                                lineHeight = 22.sp
                            )
                        } else {
                            Text(
                                text = res.errorMessage ?: "अज्ञात त्रुटि",
                                style = MaterialTheme.typography.bodyMedium,
                                color = RedDanger
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    isLoading = true
                                    aiResult = null
                                    coroutineScope.launch {
                                        aiResult = aiService.generateContent(
                                            toolType = selectedTool,
                                            userPrompt = userPrompt,
                                            selectedLanguage = selectedLanguage,
                                            businessLocation = settings?.address ?: "Shyam Arcade, Meghraj Road, Modasa"
                                        )
                                        isLoading = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("पुनः प्रयास करें (Retry)")
                            }
                        }
                    }
                }
            }
        }
    }

    // Secure Backend & Secret Setup Guide Dialog
    if (showSetupGuide) {
        AlertDialog(
            onDismissRequest = { showSetupGuide = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = GreenSuccess)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("सुरक्षित जेमिनी API सेटअप गाइड", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Text(
                            text = "१. API कुंजी सुरक्षा नियम (Zero-Leakage Policy):",
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "कभी भी जेमिनी API कुंजी को एंड्रॉयड कोड, गिटहब या सार्वजनिक फ़ाइलों में हार्डकोड न करें। APK को डीकंपाइल करके चाबी चुराई जा सकती है।",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                    item {
                        Text(
                            text = "२. सुरक्षित बैकएंड आर्किटेक्चर (Production):",
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "प्रोजेक्ट में 'backend/index.js' शामिल है। यह फ़ायरबेस क्लाउड फ़ंक्शन आपके Firebase ID Token को सत्यापित करता है और Google Cloud Secret Manager में सुरक्षित रखी गई GEMINI_API_KEY के जरिए ही जेमिनी मॉडल (gemini-3.5-flash) को कॉल करता है।",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                    item {
                        Text(
                            text = "३. Google AI Studio में चाबी बनाना:",
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "a. aistudio.google.com पर जाएं और 'Get API key' पर क्लिक करें।\nb. अपनी कुंजी को Google Cloud Secret Manager या Firebase Secrets में जोड़ें।\nc. AI Studio Build में काम करते समय Secrets Panel में GEMINI_API_KEY सेट करें।",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSetupGuide = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("समझ गया (Understood)")
                }
            }
        )
    }
}

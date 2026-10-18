package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.AiToolType
import com.example.data.ai.GeminiAiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class GeminiAiToolsTest {

    @Test
    fun aiToolType_containsAllRequiredTools() {
        val tools = AiToolType.values().toList()
        assertTrue(tools.contains(AiToolType.POSTER_GENERATOR))
        assertTrue(tools.contains(AiToolType.PRODUCT_DESCRIPTION))
        assertTrue(tools.contains(AiToolType.MARKETING_CAPTION))
        assertTrue(tools.contains(AiToolType.WHATSAPP_PROMO))
        assertTrue(tools.contains(AiToolType.FESTIVAL_GREETING))
        assertTrue(tools.contains(AiToolType.FURNITURE_DESIGN_IDEAS))
        assertTrue(tools.contains(AiToolType.CUSTOMER_REPLY_ASSISTANT))
        assertTrue(tools.contains(AiToolType.CATALOGUE_TEXT))
        assertEquals(8, tools.size)
    }

    @Test
    fun geminiAiService_handlesMissingConfigGracefullyWithoutCrashing() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
            com.google.firebase.FirebaseApp.initializeApp(
                context,
                com.google.firebase.FirebaseOptions.Builder()
                    .setApplicationId("com.example.app")
                    .setProjectId("demo-test")
                    .setApiKey("fake-key")
                    .build()
            )
        }
        val service = GeminiAiService(context)

        // When no backend or key is set, service must return clean, informative error message
        val result = service.generateContent(
            toolType = AiToolType.POSTER_GENERATOR,
            userPrompt = "Test prompt for Aravalli Furniture",
            selectedLanguage = "Hindi"
        )

        assertNotNull(result)
        // Must either fail gracefully with message or succeed if key provided
        if (!result.isSuccess) {
            assertTrue(result.errorMessage?.isNotBlank() == true)
        }
    }
}

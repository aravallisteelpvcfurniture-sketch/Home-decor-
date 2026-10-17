package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.formatInr
import com.example.ui.components.openWhatsApp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.viewmodel.AravalliViewModel

data class MarketingPost(
    val title: String,
    val category: String,
    val messageHindi: String,
    val messageEnglish: String
)

@Composable
fun CatalogueScreen(viewModel: AravalliViewModel) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isHi = currentLang == "hi"

    val marketingPosts = listOf(
        MarketingPost(
            title = "🏡 अपने सपनों का घर सजाएं - अरावल्ली स्टील पीवीसी",
            category = "General Branding",
            messageHindi = "नमस्ते! अपने घर को दें आधुनिक और मजबूत रूप अरावल्ली स्टील पीवीसी फर्नीचर (मोडासा) के साथ।\n✨ एसएस 304 बालकनी रेलिंग\n✨ 100% वाटरप्रूफ पीवीसी मॉड्यूलर किचन\n✨ यूपीवीसी खिड़कियां और अलमारी\n📍 श्याम आर्केड, मेघराज रोड, मोडासा\n📞 संपर्क: +91 98250 12345",
            messageEnglish = "Hello! Beautify your home with Aravalli Steel PVC Furniture (Modasa, Gujarat).\n✨ SS 304 Railing\n✨ 100% Waterproof PVC Kitchen\n✨ UPVC Windows & Wardrobes\n📍 Shyam Arcade, Meghraj Road, Modasa\n📞 Call/WhatsApp: +91 98250 12345"
        ),
        MarketingPost(
            title = "🛡️ एसएस 304 टफन्ड ग्लास बालकनी रेलिंग",
            category = "Steel Railing",
            messageHindi = "लंबे समय तक चलने वाली, जंग-मुक्त SS 304 ग्लास और पाइप रेलिंग। आधुनिक बंगले और सीढ़ियों के लिए विशेष डिजाइन उपलब्ध। आज ही फ्री माप और कोटेशन के लिए संपर्क करें: +91 98250 12345 - अरावल्ली स्टील, मोडासा।",
            messageEnglish = "Rust-free heavy SS 304 Toughened Glass & Pipe Railings for modern balconies and staircases. Free site measurements available in Modasa & nearby regions. Call: +91 98250 12345."
        ),
        MarketingPost(
            title = "🍳 100% वाटरप्रूफ और दीमक-मुक्त पीवीसी किचन",
            category = "Modular Kitchen",
            messageHindi = "पानी या दीमक से किचन खराब होने की चिंता छोड़ें! अरावल्ली का 18mm पीवीसी फोम शीट मॉड्यूलर किचन - सॉफ्ट क्लोज हाइड्रोलिक फिटिंग्स और हाई-ग्लॉस शटर्स के साथ। मोडासा शोरूम पर पधारें: श्याम आर्केड, मेघराज रोड।",
            messageEnglish = "Zero water damage, zero termites! Modern PVC Modular Kitchen with soft-close hydraulic baskets. Visit our showroom at Shyam Arcade, Meghraj Road, Modasa or call +91 98250 12345."
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("catalogue_screen")
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Digital Showcase Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "डिजिटल कैटलॉग एवं मार्केटिंग (Showcase)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ग्राहकों को सीधे व्हाट्सएप पर भेजें अपने फर्नीचर और रेलिंग उत्पादों के पेशेवर विवरण और प्रचार संदेश।",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            }
        }

        // Shareable Marketing Templates Section
        item {
            Text(
                text = "प्रमोशनल मैसेज और पोस्टर्स (Ready-to-Share Posts)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
        }

        items(marketingPosts) { post ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = post.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            color = GoldLight,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = post.category,
                                color = Color(0xFFB45309),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isHi) post.messageHindi else post.messageEnglish,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val msg = if (isHi) post.messageHindi else post.messageEnglish
                            openWhatsApp(context, "", msg)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                        modifier = Modifier.align(Alignment.End),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("व्हाट्सएप पर शेयर करें", fontSize = 12.sp)
                    }
                }
            }
        }

        // Product Showcase Cards
        item {
            Text(
                text = "प्रमुख उत्पाद कैटलॉग (Featured Products)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
        }

        items(products) { prod ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = prod.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "${formatInr(prod.sellingPrice)} / ${prod.unit}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${prod.category} • मटेरियल: ${prod.material}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = prod.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Button(
                        onClick = {
                            val msg = "नमस्ते! मैं अरावल्ली स्टील पीवीसी फर्नीचर के '${prod.name}' (${prod.category}) उत्पाद में रुचि रखता हूँ। कृपया अधिक जानकारी और कोटेशन साझा करें।"
                            openWhatsApp(context, "", msg)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("पूछताछ करें (Inquire on WhatsApp)")
                    }
                }
            }
        }
    }
}

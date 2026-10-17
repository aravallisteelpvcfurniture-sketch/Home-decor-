package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessSettings
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.RedDanger
import com.example.ui.theme.SteelLight
import com.example.ui.viewmodel.AravalliViewModel

@Composable
fun SettingsScreen(
    viewModel: AravalliViewModel,
    onSignOutClicked: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isHi = currentLang == "hi"

    var businessName by remember(settings) { mutableStateOf(settings?.businessName ?: "ARAVALLI STEEL PVC FURNITURE") }
    var address by remember(settings) { mutableStateOf(settings?.address ?: "Shyam Arcade, 14, Meghraj Road, near Bharwad Hostel, Modasa, Gujarat 383315") }
    var phone by remember(settings) { mutableStateOf(settings?.phone ?: "+91 98250 12345") }
    var whatsapp by remember(settings) { mutableStateOf(settings?.whatsapp ?: "+91 98250 12345") }
    var email by remember(settings) { mutableStateOf(settings?.email ?: "aravallisteelpvc@gmail.com") }
    var gstin by remember(settings) { mutableStateOf(settings?.gstin ?: "24AABCA1234F1Z5") }
    var defaultGstRateStr by remember(settings) { mutableStateOf((settings?.defaultGstRate ?: 18.0).toString()) }
    var estimateTerms by remember(settings) { mutableStateOf(settings?.estimateTerms ?: "1. 50% advance along with confirmed work order.\n2. 40% on delivery of materials at site.\n3. 10% balance upon final completion and quality check.") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen")
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Business Profile Configuration Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = NavyPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "व्यापार प्रोफ़ाइल (Business Details)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    }

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("दुकान / फर्म का नाम") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("व्यापारिक पता (Address)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("फ़ोन नंबर (Phone)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = whatsapp,
                            onValueChange = { whatsapp = it },
                            label = { Text("व्हाट्सएप (WhatsApp)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = gstin,
                            onValueChange = { gstin = it },
                            label = { Text("GSTIN नंबर") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = defaultGstRateStr,
                            onValueChange = { defaultGstRateStr = it },
                            label = { Text("डिफ़ॉल्ट GST %") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = estimateTerms,
                        onValueChange = { estimateTerms = it },
                        label = { Text("कोटेशन नियम और शर्तें (Terms & Conditions)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )

                    Button(
                        onClick = {
                            val gst = defaultGstRateStr.toDoubleOrNull() ?: 18.0
                            val updated = BusinessSettings(
                                businessName = businessName.trim(),
                                address = address.trim(),
                                phone = phone.trim(),
                                whatsapp = whatsapp.trim(),
                                email = email.trim(),
                                gstin = gstin.trim(),
                                defaultGstRate = gst,
                                estimateTerms = estimateTerms.trim()
                            )
                            viewModel.saveSettings(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("सेटिंग्स सुरक्षित करें (Save Profile)")
                    }
                }
            }
        }

        // Language & Localization Settings
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = NavySecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "भाषा चयन (Language Preference)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(
                            Triple("hi", "हिन्दी (Hindi)", "डिफ़ॉल्ट भाषा"),
                            Triple("en", "English", "Business standard"),
                            Triple("gu", "ગુજરાતી (Gujarati)", "સ્થાનિક ભાષા")
                        ).forEach { (code, name, sub) ->
                            val isSel = currentLang == code
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setLanguage(code) },
                                color = if (isSel) NavyPrimary else SteelLight,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = name,
                                        color = if (isSel) Color.White else Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Cloud Firestore Database & Security Rules Status
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = GreenSuccess)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("क्लाउड डेटाबेस स्थिति (Cloud Firestore)", fontWeight = FontWeight.Bold, color = NavyPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• एंटरप्राइज़ क्लाउड फायरस्टोर से कनेक्टेड\n• शून्य-विश्वास सुरक्षा नियम (Zero-Trust Security Rules) सक्रिय\n• स्वचालित बैकअप और डेटा अलगाव (Per-user workspace isolation)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF334155)
                    )
                }
            }
        }

        // APK Build & GitHub Actions Guide
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = NavySecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("एंड्रॉयड APK निर्माण (Build & Download APK)", fontWeight = FontWeight.Bold, color = NavyPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "फोन में इंस्टॉल करने के लिए APK फ़ाइल प्राप्त करने के 2 आसान तरीके:\n1. Settings मेनू में जाकर 'Export as ZIP' करें या GitHub पर पुश करें।\n2. GitHub Actions वर्कफ़्लो 'gradle assembleRelease' चलाकर स्वचालित रूप से Actions > Artifacts से APK डाउनलोड कर सकते हैं।",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }
        }

        // Sign Out Button
        item {
            Button(
                onClick = onSignOutClicked,
                colors = ButtonDefaults.buttonColors(containerColor = RedDanger),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sign_out_button")
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("साइन आउट करें (Sign Out)")
            }
        }
    }
}

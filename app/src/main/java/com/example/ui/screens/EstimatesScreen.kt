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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Estimate
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatInr
import com.example.ui.components.openWhatsApp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.viewmodel.AravalliViewModel

@Composable
fun EstimatesScreen(viewModel: AravalliViewModel) {
    val context = LocalContext.current
    val estimates by viewModel.estimates.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isHi = currentLang == "hi"

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedEstimateForView by remember { mutableStateOf<Estimate?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = NavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("new_estimate_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Quote")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (estimates.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isHi) "अभी कोई कोटेशन नहीं है। नया कोटेशन बनाएं!" else "No estimates yet. Create a quote!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(estimates, key = { it.id.ifBlank { it.estimateNumber } }) { est ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedEstimateForView = est },
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
                                    Column {
                                        Text(
                                            text = "#${est.estimateNumber}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = NavySecondary
                                        )
                                        Text(
                                            text = est.customerName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyPrimary
                                        )
                                    }
                                    StatusBadge(status = est.status)
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${est.projectType} • फोन: ${est.customerPhone.ifBlank { "N/A" }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "कुल राशि (Grand Total):",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = formatInr(est.finalTotal),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D)
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(
                                            onClick = {
                                                val msg = "अरावल्ली स्टील पीवीसी फर्नीचर (मोडासा):\n" +
                                                        "कोटेशन #${est.estimateNumber}\n" +
                                                        "ग्राहक: ${est.customerName}\n" +
                                                        "प्रोजेक्ट: ${est.projectType}\n" +
                                                        "कुल राशि: ${formatInr(est.finalTotal)}\n" +
                                                        "एडवांस: ${formatInr(est.advance)}\n" +
                                                        "बाकी: ${formatInr(est.balance)}\n" +
                                                        "दुकान: श्याम आर्केड, मेघराज रोड, मोडासा (गुजरात)\n" +
                                                        "फोन: +91 98250 12345"
                                                openWhatsApp(context, est.customerPhone, msg)
                                            }
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = "Share", tint = NavySecondary)
                                        }

                                        if (est.status != "Converted") {
                                            Button(
                                                onClick = {
                                                    viewModel.convertEstimateToOrder(est)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text("ऑर्डर बनाएं", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // View Estimate Detail Dialog
    selectedEstimateForView?.let { est ->
        AlertDialog(
            onDismissRequest = { selectedEstimateForView = null },
            title = {
                Column {
                    Text("ARAVALLI STEEL PVC FURNITURE", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 16.sp)
                    Text("कोटेशन #${est.estimateNumber}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Text("ग्राहक का नाम: ${est.customerName}", fontWeight = FontWeight.Bold)
                        Text("फोन: ${est.customerPhone}")
                        Text("प्रोजेक्ट: ${est.projectType}")
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("उप-योग (Subtotal):")
                            Text(formatInr(est.subtotal), fontWeight = FontWeight.Bold)
                        }
                        if (est.gstPercent > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("जीएसटी (${est.gstPercent}%):")
                                Text(formatInr(est.gstAmount))
                            }
                        }
                        if (est.discount > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("छूट (Discount):")
                                Text("-${formatInr(est.discount)}", color = Color(0xFFDC2626))
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("कुल राशि (Total):", fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                            Text(formatInr(est.finalTotal), fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("एडवांस देय (Advance):")
                            Text(formatInr(est.advance))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("बाकी राशि (Balance):")
                            Text(formatInr(est.balance), fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                        }
                    }
                    item {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        Text("शर्तें (Terms):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = settings?.estimateTerms ?: "1. 50% एडवांस आर्डर के समय.\n2. 40% माल साइट पर आने पर.\n3. 10% कार्य पूर्ण होने पर.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                }
            },
            confirmButton = {
                if (est.status != "Converted") {
                    Button(
                        onClick = {
                            viewModel.convertEstimateToOrder(est) {
                                selectedEstimateForView = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text("ऑर्डर में बदलें (Convert to Order)")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedEstimateForView = null }) {
                    Text("बंद करें (Close)")
                }
            }
        )
    }

    if (showCreateDialog) {
        CreateEstimateDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { newEst ->
                viewModel.saveEstimate(newEst) {
                    showCreateDialog = false
                }
            }
        )
    }
}

@Composable
fun CreateEstimateDialog(
    onDismiss: () -> Unit,
    onConfirm: (Estimate) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var projectType by remember { mutableStateOf("SS Railing") }
    var subtotalStr by remember { mutableStateOf("") }
    var gstStr by remember { mutableStateOf("18") }
    var advanceStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("नया कोटेशन बनाएं (New Quote)", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("ग्राहक का नाम (Customer Name)*") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("फोन नंबर (Phone)*") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = projectType,
                        onValueChange = { projectType = it },
                        label = { Text("प्रोजेक्ट प्रकार (SS Railing, Kitchen, Wardrobe)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = subtotalStr,
                        onValueChange = { subtotalStr = it },
                        label = { Text("अनुमानित राशि (Amount ₹)*") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = gstStr,
                            onValueChange = { gstStr = it },
                            label = { Text("GST %") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = advanceStr,
                            onValueChange = { advanceStr = it },
                            label = { Text("एडवांस (Advance ₹)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("टिप्पणी / माप विवरण (Notes)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val subtotal = subtotalStr.toDoubleOrNull() ?: 0.0
                    val gstPercent = gstStr.toDoubleOrNull() ?: 0.0
                    val gstAmount = subtotal * (gstPercent / 100.0)
                    val finalTotal = subtotal + gstAmount
                    val advance = advanceStr.toDoubleOrNull() ?: (finalTotal * 0.5)
                    val balance = (finalTotal - advance).coerceAtLeast(0.0)
                    val estNum = "EST-" + System.currentTimeMillis().toString().takeLast(6)

                    if (customerName.isNotBlank() && subtotal > 0) {
                        onConfirm(
                            Estimate(
                                estimateNumber = estNum,
                                customerName = customerName.trim(),
                                customerPhone = customerPhone.trim(),
                                projectType = projectType.trim(),
                                subtotal = subtotal,
                                gstPercent = gstPercent,
                                gstAmount = gstAmount,
                                finalTotal = finalTotal,
                                advance = advance,
                                balance = balance,
                                notes = notes.trim(),
                                status = "Draft"
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("सुरक्षित करें (Save)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("रद्द करें (Cancel)") }
        }
    )
}

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.model.Customer
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatInr
import com.example.ui.components.openDialer
import com.example.ui.components.openWhatsApp
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SteelLight
import com.example.ui.viewmodel.AravalliViewModel

@Composable
fun CustomersScreen(viewModel: AravalliViewModel) {
    val context = LocalContext.current
    val customers by viewModel.customers.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isHi = currentLang == "hi"

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }

    val statusTabs = listOf("All", "New Lead", "Site Visit", "Estimate Sent", "Confirmed", "Completed")

    val filteredCustomers = customers.filter {
        (selectedStatus == "All" || it.status == selectedStatus) &&
        (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.villageOrCity.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery))
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = NavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_customer_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Customer")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("customer_search_input"),
                placeholder = { Text(if (isHi) "ग्राहक या गांव/शहर खोजें (Search customer, city...)" else "Search customer...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Status Filter Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statusTabs) { tab ->
                    val isSelected = selectedStatus == tab
                    Surface(
                        modifier = Modifier.clickable { selectedStatus = tab },
                        color = if (isSelected) NavyPrimary else SteelLight,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) Color.White else Color.Black,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            if (filteredCustomers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Group, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isHi) "कोई ग्राहक या पूछताछ नहीं मिली" else "No customers or leads found",
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredCustomers, key = { it.id.ifBlank { it.name } }) { customer ->
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
                                    Column {
                                        Text(
                                            text = customer.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyPrimary
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${customer.villageOrCity} • ${customer.address.ifBlank { "मोडासा" }}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                    StatusBadge(status = customer.status)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "प्रोजेक्ट: ${customer.projectType}  |  बजट: ${if (customer.budget > 0) formatInr(customer.budget) else "विचारधीन"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF334155)
                                )

                                if (customer.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "टिप्पणी: ${customer.notes}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = customer.phone,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = NavySecondary
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(
                                            onClick = { openDialer(context, customer.phone) }
                                        ) {
                                            Icon(Icons.Default.Phone, contentDescription = "Call", tint = NavySecondary)
                                        }

                                        IconButton(
                                            onClick = {
                                                val msg = "नमस्ते ${customer.name} जी! अरावल्ली स्टील पीवीसी फर्नीचर (मोडासा, गुजरात) की ओर से संपर्क कर रहे हैं। आपके ${customer.projectType} प्रोजेक्ट के बारे में बातचीत करने हेतु कृपया बताएं कब अनुकूल रहेगा।"
                                                openWhatsApp(context, customer.whatsapp.ifBlank { customer.phone }, msg)
                                            }
                                        ) {
                                            Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = GreenSuccess)
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

    if (showAddDialog) {
        AddCustomerDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { newCust ->
                viewModel.addOrUpdateCustomer(newCust) {
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
fun AddCustomerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Customer) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Modasa") }
    var address by remember { mutableStateOf("") }
    var projectType by remember { mutableStateOf("SS Railing") }
    var budgetStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("नया ग्राहक जोड़ें (Add Customer / Lead)", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("ग्राहक का नाम (Customer Name)*") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("फोन नंबर (Phone)*") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = whatsapp,
                            onValueChange = { whatsapp = it },
                            label = { Text("व्हाट्सएप (WhatsApp)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("गांव / शहर (City/Village)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = budgetStr,
                            onValueChange = { budgetStr = it },
                            label = { Text("अनुमानित बजट (₹)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
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
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("पूरा पता / लैंडमार्क (Address)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("माप विवरण / टिप्पणी (Requirements Notes)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val budget = budgetStr.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onConfirm(
                            Customer(
                                name = name.trim(),
                                phone = phone.trim(),
                                whatsapp = whatsapp.ifBlank { phone }.trim(),
                                villageOrCity = city.trim(),
                                address = address.trim(),
                                projectType = projectType.trim(),
                                budget = budget,
                                status = "New Lead",
                                notes = notes.trim()
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
            TextButton(onClick = onDismiss) { Text("रद्द करें") }
        }
    )
}

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.example.data.model.Product
import com.example.ui.components.formatInr
import com.example.ui.components.openWhatsApp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SteelLight
import com.example.ui.viewmodel.AravalliViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(viewModel: AravalliViewModel) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isHi = currentLang == "hi"

    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf(
        "All",
        "Steel Railing",
        "Modular Kitchen",
        "Wardrobe",
        "PVC Furniture",
        "Aluminium Glass",
        "UPVC Doors Windows",
        "TV Unit"
    )

    val filteredProducts = products.filter {
        (selectedCategory == "All" || it.category == selectedCategory) &&
        (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.material.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = NavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_product_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
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
                    .testTag("product_search_input"),
                placeholder = { Text(if (isHi) "उत्पाद खोजें (Search products, material...)" else "Search products...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Category Filter Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        modifier = Modifier.clickable { selectedCategory = cat },
                        color = if (isSelected) NavyPrimary else SteelLight,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else Color.Black,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Products List
            if (filteredProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isHi) "कोई उत्पाद नहीं मिला" else "No products found",
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts, key = { it.id.ifBlank { it.name } }) { product ->
                        ProductCard(
                            product = product,
                            onShare = {
                                val msg = "अरावल्ली स्टील पीवीसी फर्नीचर (मोडासा):\n" +
                                        "उत्पाद: ${product.name}\n" +
                                        "श्रेणी: ${product.category}\n" +
                                        "मटेरियल: ${product.material}\n" +
                                        "मूल्य: ${formatInr(product.sellingPrice)} प्रति ${product.unit}\n" +
                                        "विवरण: ${product.description}\n" +
                                        "संपर्क करें: +91 98250 12345"
                                openWhatsApp(context, "", msg)
                            },
                            onDelete = {
                                viewModel.removeProduct(product.id)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddProductDialog(
            categories = categories.filter { it != "All" },
            onDismiss = { showAddDialog = false },
            onConfirm = { newProduct ->
                viewModel.addOrUpdateProduct(newProduct) {
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Text(
                        text = "${product.category} • SKU: ${product.sku.ifBlank { "N/A" }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = NavySecondary)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF334155),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "मूल्य (Selling Price)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "${formatInr(product.sellingPrice)} / ${product.unit}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "स्टॉक: ${product.stockQuantity.toInt()} ${product.unit}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (product.stockQuantity > 5) Color(0xFF0F172A) else Color(0xFFDC2626)
                    )
                    Text(
                        text = "लागत: ${formatInr(product.costPrice)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (Product) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories.firstOrNull() ?: "Steel Railing") }
    var material by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("Rft") }
    var sellingPriceStr by remember { mutableStateOf("") }
    var costPriceStr by remember { mutableStateOf("") }
    var stockStr by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var expandedCategory by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("नया उत्पाद जोड़ें (Add Product)", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("उत्पाद का नाम (Product Name)*") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = !expandedCategory }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("श्रेणी (Category)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            categories.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c) },
                                    onClick = {
                                        category = c
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = sku,
                            onValueChange = { sku = it },
                            label = { Text("SKU कोड") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("इकाई (Unit)*") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = sellingPriceStr,
                            onValueChange = { sellingPriceStr = it },
                            label = { Text("विक्रय मूल्य (Sell)*") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = costPriceStr,
                            onValueChange = { costPriceStr = it },
                            label = { Text("लागत (Cost)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = stockStr,
                        onValueChange = { stockStr = it },
                        label = { Text("स्टॉक मात्रा (Stock Qty)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = material,
                        onValueChange = { material = it },
                        label = { Text("मटेरियल (SS 304, PVC, Aluminium)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("विवरण (Description)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sell = sellingPriceStr.toDoubleOrNull() ?: 0.0
                    val cost = costPriceStr.toDoubleOrNull() ?: 0.0
                    val stock = stockStr.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        onConfirm(
                            Product(
                                name = name.trim(),
                                sku = sku.trim(),
                                category = category,
                                material = material.trim(),
                                unit = unit.trim(),
                                sellingPrice = sell,
                                costPrice = cost,
                                stockQuantity = stock,
                                description = description.trim()
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
            TextButton(onClick = onDismiss) {
                Text("रद्द करें (Cancel)")
            }
        }
    )
}

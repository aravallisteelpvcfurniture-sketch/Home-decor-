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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.data.model.Estimate
import com.example.domain.calculator.FurnitureCalculators
import com.example.ui.components.formatInr
import com.example.ui.components.openWhatsApp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SteelLight
import com.example.ui.viewmodel.AravalliViewModel

@Composable
fun ToolsScreen(
    viewModel: AravalliViewModel,
    onNavigateToEstimates: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isHi = currentLang == "hi"

    val toolTabs = listOf(
        "एसएस रेलिंग (SS Railing)",
        "एल्युमिनियम बालकनी (Balcony)",
        "पीवीसी फर्नीचर (PVC Furniture)",
        "मॉड्यूलर किचन (Kitchen)",
        "यूनिट कनवर्टर (Converter)"
    )
    var selectedTab by remember { mutableStateOf(toolTabs[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("tools_screen")
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Selector Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(toolTabs) { tab ->
                val isSelected = selectedTab == tab
                Surface(
                    modifier = Modifier.clickable { selectedTab = tab },
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

        when (selectedTab) {
            toolTabs[0] -> SsRailingCalculator(viewModel, onNavigateToEstimates)
            toolTabs[1] -> AluminiumBalconyCalculator(viewModel, onNavigateToEstimates)
            toolTabs[2] -> PvcFurnitureCalculator(viewModel, onNavigateToEstimates)
            toolTabs[3] -> ModularKitchenCalculator(viewModel, onNavigateToEstimates)
            toolTabs[4] -> UnitConverterTool()
        }
    }
}

@Composable
fun SsRailingCalculator(
    viewModel: AravalliViewModel,
    onNavigateToEstimates: () -> Unit
) {
    val context = LocalContext.current
    var rftStr by remember { mutableStateOf("45") }
    var heightStr by remember { mutableStateOf("36") }
    var isGlass by remember { mutableStateOf(true) }
    var rateStr by remember { mutableStateOf("1650") }
    var glassRateStr by remember { mutableStateOf("180") }
    var installRateStr by remember { mutableStateOf("150") }
    var includeGst by remember { mutableStateOf(true) }

    val rft = rftStr.toDoubleOrNull() ?: 0.0
    val height = heightStr.toDoubleOrNull() ?: 36.0
    val rate = rateStr.toDoubleOrNull() ?: 1650.0
    val glassRate = glassRateStr.toDoubleOrNull() ?: 180.0
    val installRate = installRateStr.toDoubleOrNull() ?: 150.0
    val gst = if (includeGst) 18.0 else 0.0

    val result = FurnitureCalculators.calculateSsRailing(
        runningFeet = rft,
        heightInches = height,
        isGlassRailing = isGlass,
        ratePerRft = rate,
        glassRatePerSqFt = glassRate,
        installationPerRft = installRate,
        gstPercent = gst
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "एसएस रेलिंग माप और दर (SS Railing Inputs)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = rftStr,
                            onValueChange = { rftStr = it },
                            label = { Text("लंबाई (Running Feet)*") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = heightStr,
                            onValueChange = { heightStr = it },
                            label = { Text("ऊंचाई (इंच / Inch)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isGlass) "12mm टफन्ड ग्लास रेलिंग (Glass Railing)" else "एसएस पाइप बैलस्टर रेलing (Pipes Railing)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(checked = isGlass, onCheckedChange = { isGlass = it })
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = rateStr,
                            onValueChange = { rateStr = it },
                            label = { Text("रेलिंग दर (₹ / Rft)") },
                            modifier = Modifier.weight(1f)
                        )
                        if (isGlass) {
                            OutlinedTextField(
                                value = glassRateStr,
                                onValueChange = { glassRateStr = it },
                                label = { Text("ग्लास दर (₹ / SqFt)") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("18% GST शामिल करें (Include 18% GST)", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = includeGst, onCheckedChange = { includeGst = it })
                    }
                }
            }
        }

        // Calculation Breakdown Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "गणना का विवरण (Calculation Breakdown)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    CalcRow("कुल लंबाई (Total Rft):", "${result.runningFeet} Rft", Color.White)
                    if (isGlass) {
                        CalcRow("ग्लास क्षेत्रफल (Glass Area):", "${result.glassSqFt} Sq Ft", Color.White)
                        CalcRow("ग्लास लागत (Glass Cost):", formatInr(result.glassCost), Color.White)
                    }
                    CalcRow("रेलिंग सामग्री (Railing Base):", formatInr(result.baseRailingCost), Color.White)
                    CalcRow("फैब्रिकेशन चार्ज (Fabrication):", formatInr(result.fabricationCharges), Color.White)
                    CalcRow("फिटिंग चार्ज (Installation):", formatInr(result.installationCharges), Color.White)
                    CalcRow("उप-योग (Subtotal):", formatInr(result.subtotal), GoldAccent)
                    if (includeGst) {
                        CalcRow("जीएसटी 18% (GST):", formatInr(result.gstAmount), Color.White)
                    }

                    Divider(color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "कुल राशि (Grand Total):",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF86EFAC)
                        )
                        Text(
                            text = formatInr(result.grandTotal),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF86EFAC)
                        )
                    }
                }
            }
        }

        // Action Buttons: Share & Save Quote
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        val shareText = "अरावल्ली स्टील पीवीसी फर्नीचर (मोडासा, गुजरात)\n" +
                                "एसएस रेलिंग कोटेशन अनुमान:\n" +
                                "लंबाई: ${result.runningFeet} Rft (${result.style})\n" +
                                "उप-योग: ${formatInr(result.subtotal)}\n" +
                                "कुल राशि: ${formatInr(result.grandTotal)}\n" +
                                "संपर्क: +91 98250 12345 (श्याम आर्केड, मेघराज रोड, मोडासा)"
                        openWhatsApp(context, "", shareText)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = NavySecondary)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("शेयर (WhatsApp)")
                }

                Button(
                    onClick = {
                        val estNumber = "EST-" + System.currentTimeMillis().toString().takeLast(6)
                        val est = Estimate(
                            estimateNumber = estNumber,
                            customerName = "Railing Enquiry",
                            projectType = result.style,
                            subtotal = result.subtotal,
                            gstPercent = if (includeGst) 18.0 else 0.0,
                            gstAmount = result.gstAmount,
                            finalTotal = result.grandTotal,
                            advance = result.grandTotal * 0.5,
                            balance = result.grandTotal * 0.5,
                            notes = "Length: ${result.runningFeet} Rft @ ₹${result.ratePerRft}"
                        )
                        viewModel.saveEstimate(est) {
                            onNavigateToEstimates()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
                ) {
                    Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("कोटेशन बनाएं")
                }
            }
        }
    }
}

@Composable
fun AluminiumBalconyCalculator(
    viewModel: AravalliViewModel,
    onNavigateToEstimates: () -> Unit
) {
    var widthStr by remember { mutableStateOf("12") }
    var heightStr by remember { mutableStateOf("4") }
    var rateStr by remember { mutableStateOf("420") }
    var hardwareStr by remember { mutableStateOf("3500") }

    val width = widthStr.toDoubleOrNull() ?: 0.0
    val height = heightStr.toDoubleOrNull() ?: 0.0
    val rate = rateStr.toDoubleOrNull() ?: 420.0
    val hardware = hardwareStr.toDoubleOrNull() ?: 3500.0

    val result = FurnitureCalculators.calculateAluminiumBalcony(
        widthFeet = width,
        heightFeet = height,
        ratePerSqFt = rate,
        hardwareCost = hardware
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("एल्युमिनियम और ग्लास बालकनी (Aluminium Glass Balcony)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(value = widthStr, onValueChange = { widthStr = it }, label = { Text("चौड़ाई (Width Feet)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = heightStr, onValueChange = { heightStr = it }, label = { Text("ऊंचाई (Height Feet)") }, modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(value = rateStr, onValueChange = { rateStr = it }, label = { Text("दर (₹ / SqFt)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = hardwareStr, onValueChange = { hardwareStr = it }, label = { Text("हार्डवेयर / फिटिंग (₹)") }, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = NavyPrimary), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("बालकनी लागत विवरण (Cost Details)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    CalcRow("कुल क्षेत्रफल (Area):", "${result.areaSqFt} Sq Ft", Color.White)
                    CalcRow("सामग्री लागत (Material):", formatInr(result.materialCost), Color.White)
                    CalcRow("हार्डवेयर और स्पाइडर फिटिंग:", formatInr(result.hardwareCost), Color.White)
                    CalcRow("मजदूरी (Labour):", formatInr(result.labourCost), Color.White)
                    CalcRow("जीएसटी 18% (GST):", formatInr(result.gstAmount), Color.White)
                    Divider(color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("कुल राशि (Total):", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF86EFAC))
                        Text(formatInr(result.grandTotal), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF86EFAC))
                    }
                }
            }
        }
    }
}

@Composable
fun PvcFurnitureCalculator(
    viewModel: AravalliViewModel,
    onNavigateToEstimates: () -> Unit
) {
    var lengthStr by remember { mutableStateOf("6") }
    var heightStr by remember { mutableStateOf("7") }
    var rateStr by remember { mutableStateOf("950") }
    var fittingsStr by remember { mutableStateOf("4500") }

    val length = lengthStr.toDoubleOrNull() ?: 0.0
    val height = heightStr.toDoubleOrNull() ?: 0.0
    val rate = rateStr.toDoubleOrNull() ?: 950.0
    val fittings = fittingsStr.toDoubleOrNull() ?: 4500.0

    val result = FurnitureCalculators.calculatePvcFurniture(
        lengthFeet = length,
        heightFeet = height,
        ratePerSqFt = rate,
        fittingsCost = fittings
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("पीवीसी अलमारी व फर्नीचर कैलकुलेटर (PVC Furniture)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(value = lengthStr, onValueChange = { lengthStr = it }, label = { Text("लंबाई (Length Feet)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = heightStr, onValueChange = { heightStr = it }, label = { Text("ऊंचाई (Height Feet)") }, modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(value = rateStr, onValueChange = { rateStr = it }, label = { Text("दर (₹ / SqFt)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = fittingsStr, onValueChange = { fittingsStr = it }, label = { Text("फिटिंग्स / हैंडल्स (₹)") }, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = NavyPrimary), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("पीवीसी फर्नीचर लागत विवरण", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    CalcRow("कुल क्षेत्रफल (Total Sq Ft):", "${result.totalSqFt} Sq Ft", Color.White)
                    CalcRow("18mm वाटरप्रूफ शीट लागत:", formatInr(result.materialCost), Color.White)
                    CalcRow("फिटिंग्स और लॉकर्स:", formatInr(result.fittingsCost), Color.White)
                    CalcRow("इंस्टॉलेशन चार्ज:", formatInr(result.installationCost), Color.White)
                    Divider(color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("कुल राशि (Total):", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF86EFAC))
                        Text(formatInr(result.grandTotal), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF86EFAC))
                    }
                }
            }
        }
    }
}

@Composable
fun ModularKitchenCalculator(
    viewModel: AravalliViewModel,
    onNavigateToEstimates: () -> Unit
) {
    var rftStr by remember { mutableStateOf("12") }
    var baseRateStr by remember { mutableStateOf("2200") }
    var wallRateStr by remember { mutableStateOf("1800") }
    var accessoriesStr by remember { mutableStateOf("18500") }
    var hasWall by remember { mutableStateOf(true) }

    val rft = rftStr.toDoubleOrNull() ?: 0.0
    val baseRate = baseRateStr.toDoubleOrNull() ?: 2200.0
    val wallRate = wallRateStr.toDoubleOrNull() ?: 1800.0
    val acc = accessoriesStr.toDoubleOrNull() ?: 18500.0

    val result = FurnitureCalculators.calculateModularKitchen(
        runningFeet = rft,
        hasWallCabinets = hasWall,
        baseRatePerRft = baseRate,
        wallRatePerRft = wallRate,
        basketAccessoriesCost = acc
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("वाटरप्रूफ पीवीसी मॉड्यूलर किचन (Modular Kitchen)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    OutlinedTextField(value = rftStr, onValueChange = { rftStr = it }, label = { Text("किचन लंबाई (Running Feet)*") }, modifier = Modifier.fillMaxWidth())

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("दीवार के ऊपर के कैबिनेट (Wall Cabinets)", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = hasWall, onCheckedChange = { hasWall = it })
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(value = baseRateStr, onValueChange = { baseRateStr = it }, label = { Text("नीचे कैबिनेट दर (₹/Rft)") }, modifier = Modifier.weight(1f))
                        if (hasWall) {
                            OutlinedTextField(value = wallRateStr, onValueChange = { wallRateStr = it }, label = { Text("ऊपर कैबिनेट दर (₹/Rft)") }, modifier = Modifier.weight(1f))
                        }
                    }

                    OutlinedTextField(value = accessoriesStr, onValueChange = { accessoriesStr = it }, label = { Text("एसएस 304 बास्केट और टैंडम बॉक्स (₹)") }, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = NavyPrimary), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("किचन लागत विवरण (Kitchen Breakdown)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    CalcRow("कुल लंबाई (Running Feet):", "${result.runningFeet} Rft", Color.White)
                    CalcRow("नीचे के कैबिनेट (Base Cabinets):", formatInr(result.baseCabinetCost), Color.White)
                    if (hasWall) {
                        CalcRow("ऊपर के कैबिनेट (Wall Cabinets):", formatInr(result.wallCabinetCost), Color.White)
                    }
                    CalcRow("एसएस 304 बास्केट एक्सेसरीज:", formatInr(result.basketAccessoriesCost), Color.White)
                    Divider(color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("कुल राशि (Total):", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF86EFAC))
                        Text(formatInr(result.grandTotal), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF86EFAC))
                    }
                }
            }
        }
    }
}

@Composable
fun UnitConverterTool() {
    var inputStr by remember { mutableStateOf("100") }
    var selectedUnit by remember { mutableStateOf("mm") }
    var heightStr by remember { mutableStateOf("5") }

    val input = inputStr.toDoubleOrNull() ?: 0.0
    val height = heightStr.toDoubleOrNull() ?: 0.0
    val conv = FurnitureCalculators.convertUnits(input, selectedUnit, height)

    val units = listOf("mm", "inch", "feet", "cm", "meter")

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("फीट-इंच-मिमी इकाई परिवर्तक (Unit Converter)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    OutlinedTextField(
                        value = inputStr,
                        onValueChange = { inputStr = it },
                        label = { Text("माप दर्ज करें (Enter Value)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("इकाई चुनें (From Unit):", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        units.forEach { u ->
                            val isSel = selectedUnit == u
                            Surface(
                                modifier = Modifier.clickable { selectedUnit = u },
                                color = if (isSel) NavyPrimary else SteelLight,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = u.uppercase(),
                                    color = if (isSel) Color.White else Color.Black,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = heightStr,
                        onValueChange = { heightStr = it },
                        label = { Text("ऊंचाई फीट में (क्षेत्रफल के लिए / Height in ft)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = NavySecondary), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("परिवर्तित मान (Converted Values)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    CalcRow("मिलीमीटर (Millimetres):", "${conv.mm} mm", Color.White)
                    CalcRow("सेंटीमीटर (Centimetres):", "${conv.cm} cm", Color.White)
                    CalcRow("इंच (Inches):", "${conv.inches} in", Color.White)
                    CalcRow("फीट (Feet):", "${conv.feet} ft", GoldAccent)
                    if (height > 0) {
                        Divider(color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
                        CalcRow("क्षेत्रफल (Square Feet):", "${conv.sqFeet} Sq Ft", Color(0xFF86EFAC))
                    }
                }
            }
        }
    }
}

@Composable
fun CalcRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

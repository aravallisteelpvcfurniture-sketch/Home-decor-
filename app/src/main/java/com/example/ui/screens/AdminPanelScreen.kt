package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessSettings
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.Worker
import com.example.ui.components.formatInr
import com.example.ui.components.openDialer
import com.example.ui.components.openWhatsApp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.OrangeWarning
import com.example.ui.theme.RedDanger
import com.example.ui.theme.SteelLight
import com.example.ui.viewmodel.AravalliViewModel

@Composable
fun AdminPanelScreen(
    viewModel: AravalliViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isHi = currentLang == "hi"

    val customers by viewModel.customers.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val products by viewModel.products.collectAsState()
    val workers by viewModel.workers.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = if (isHi) {
        listOf("📊 अवलोकन", "👥 स्टाफ व टीम", "💰 मूल्य व दर", "📋 ऑर्डर नियंत्रण", "⚙️ दुकान व फ़र्म")
    } else {
        listOf("📊 Overview", "👥 Staff & Team", "💰 Rates & GST", "📋 Orders Control", "⚙️ Shop Info")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Admin Master Header Banner
        Surface(
            color = NavyPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("admin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GoldAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin",
                            tint = NavyPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHi) "व्यवस्थापक पैनल (Admin Panel)" else "Admin Control Center",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(GreenSuccess)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHi) "अरावल्ली स्टील पीवीसी • मोडासा" else "Aravalli Steel PVC • Modasa",
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldLight,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Surface(
                        color = NavySecondary,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "OWNER",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Navigation Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = NavySecondary,
            contentColor = Color.White,
            edgePadding = 8.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) GoldAccent else Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> AdminOverviewTab(
                    viewModel = viewModel,
                    isHi = isHi,
                    onNavigateToTab = { selectedTab = it }
                )
                1 -> AdminStaffTab(
                    viewModel = viewModel,
                    workers = workers,
                    isHi = isHi
                )
                2 -> AdminRatesTab(
                    viewModel = viewModel,
                    products = products,
                    settings = settings,
                    isHi = isHi
                )
                3 -> AdminOrdersTab(
                    viewModel = viewModel,
                    orders = orders,
                    isHi = isHi
                )
                4 -> AdminSettingsTab(
                    viewModel = viewModel,
                    settings = settings,
                    isHi = isHi
                )
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 0: OVERVIEW & MASTER KPIS
// -------------------------------------------------------------
@Composable
fun AdminOverviewTab(
    viewModel: AravalliViewModel,
    isHi: Boolean,
    onNavigateToTab: (Int) -> Unit
) {
    val context = LocalContext.current
    val customers by viewModel.customers.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val products by viewModel.products.collectAsState()
    val workers by viewModel.workers.collectAsState()
    val settings by viewModel.settings.collectAsState()

    val todaySales = viewModel.getTodaySales()
    val monthlySales = viewModel.getMonthlySales()
    val totalExpenses = viewModel.getTotalExpenses()
    val outstanding = viewModel.getOutstandingReceivables()
    val netProfit = (monthlySales - totalExpenses).coerceAtLeast(0.0)

    val activeOrders = orders.filter { it.status != "Completed" && it.status != "Cancelled" }
    val lowStockProducts = products.filter { it.stockQuantity <= 50.0 }

    var showReseedDialog by remember { mutableStateOf(false) }

    if (showReseedDialog) {
        AlertDialog(
            onDismissRequest = { showReseedDialog = false },
            title = {
                Text(if (isHi) "डेमो डेटा री-सीड करें?" else "Re-seed Factory Data?")
            },
            text = {
                Text(
                    if (isHi)
                        "यह अरावल्ली स्टील व पीवीसी के उत्पाद, कारीगर और ग्राहक डेटा को ताज़ा कर देगा। क्या आप आगे बढ़ना चाहते हैं?"
                    else
                        "This will populate fresh official products, workers and customer leads. Do you want to proceed?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.reseedDemoData()
                        showReseedDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text(if (isHi) "हाँ, री-सीड करें" else "Yes, Re-seed")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReseedDialog = false }) {
                    Text(if (isHi) "रद्द करें" else "Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Business Quick Metrics Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = NavyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHi) "व्यापार वित्तीय अवलोकन (Financial Summary)" else "Financial Master Overview",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        AdminStatItem(
                            label = if (isHi) "आज की बिक्री" else "Today's Sales",
                            value = formatInr(todaySales),
                            color = GreenSuccess,
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatItem(
                            label = if (isHi) "मासिक बिक्री" else "Monthly Sales",
                            value = formatInr(monthlySales),
                            color = NavyPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = SteelLight)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        AdminStatItem(
                            label = if (isHi) "मासिक खर्चे" else "Monthly Expense",
                            value = formatInr(totalExpenses),
                            color = RedDanger,
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatItem(
                            label = if (isHi) "अनुमानित लाभ" else "Est. Net Profit",
                            value = formatInr(netProfit),
                            color = GoldAccent,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = SteelLight)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = if (isHi) "ग्राहकों से बकाया (Outstanding Receivables)" else "Pending Customer Receivables",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                text = formatInr(outstanding),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = OrangeWarning
                            )
                        }
                        Button(
                            onClick = { onNavigateToTab(3) },
                            colors = ButtonDefaults.buttonColors(containerColor = NavySecondary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(if (isHi) "ऑर्डर देखें" else "View Orders", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Live Operational Counts
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHi) "कारखाना व वर्कशॉप स्थिति (Operations Status)" else "Live Workshop Operations",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminCountBox(
                            count = activeOrders.size.toString(),
                            label = if (isHi) "सक्रिय ऑर्डर" else "Active Orders",
                            icon = Icons.Default.Assignment,
                            color = NavyPrimary,
                            onClick = { onNavigateToTab(3) },
                            modifier = Modifier.weight(1f)
                        )
                        AdminCountBox(
                            count = workers.size.toString(),
                            label = if (isHi) "कारीगर व स्टाफ" else "Staff On Duty",
                            icon = Icons.Default.Engineering,
                            color = GoldAccent,
                            onClick = { onNavigateToTab(1) },
                            modifier = Modifier.weight(1f)
                        )
                        AdminCountBox(
                            count = customers.size.toString(),
                            label = if (isHi) "ग्राहक लीड्स" else "Total Leads",
                            icon = Icons.Default.Badge,
                            color = GreenSuccess,
                            onClick = { /* customers */ },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Low stock or alert warnings
        if (lowStockProducts.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = OrangeWarning,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHi) "कम स्टॉक चेतावनी (${lowStockProducts.size} उत्पाद)" else "Low Stock Alert (${lowStockProducts.size} items)",
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = if (isHi) "स्टॉक री-ऑर्डर करने के लिए दर सूची खोलें" else "Check rates tab to update inventory",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )
                        }
                        Button(
                            onClick = { onNavigateToTab(2) },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeWarning),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(if (isHi) "जाँचें" else "Check", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Quick Admin Utilities (Database Re-seed & Business Summary)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHi) "एडमिन टूलकिट (Admin Database & Tools)" else "Admin Database & Actions",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showReseedDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isHi) "री-सीड डेटा" else "Re-seed Data", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                val summary = """
                                    *ARAVALLI STEEL PVC FURNITURE & INTERIOR*
                                    📍 Shyam Arcade, Meghraj Road, Modasa
                                    ---------------------------------------
                                    📊 Today's Sales: ${formatInr(todaySales)}
                                    📈 Monthly Total: ${formatInr(monthlySales)}
                                    💰 Expenses: ${formatInr(totalExpenses)}
                                    ⏳ Pending Collection: ${formatInr(outstanding)}
                                    📋 Active Orders: ${activeOrders.size}
                                    👥 Active Workers: ${workers.size}
                                    ---------------------------------------
                                    App Admin Status: Active Cloud Firestore
                                """.trimIndent()

                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Aravalli Summary", summary)
                                clipboard.setPrimaryClip(clip)
                                viewModel.showMessage("व्यवसाय रिपोर्ट कॉपी की गई! (Copied to Clipboard)")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isHi) "रिपोर्ट कॉपी" else "Copy Report", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatItem(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun AdminCountBox(
    count: String,
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = SteelLight,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = count, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyPrimary)
            Text(text = label, fontSize = 10.sp, color = Color.DarkGray, maxLines = 1)
        }
    }
}

// -------------------------------------------------------------
// TAB 1: STAFF & ROLE MANAGEMENT
// -------------------------------------------------------------
@Composable
fun AdminStaffTab(
    viewModel: AravalliViewModel,
    workers: List<Worker>,
    isHi: Boolean
) {
    val context = LocalContext.current
    var showAddWorkerDialog by remember { mutableStateOf(false) }
    var editingWorker by remember { mutableStateOf<Worker?>(null) }
    var workerToDelete by remember { mutableStateOf<Worker?>(null) }

    if (showAddWorkerDialog || editingWorker != null) {
        val targetWorker = editingWorker ?: Worker()
        var name by remember { mutableStateOf(targetWorker.name) }
        var phone by remember { mutableStateOf(targetWorker.phone) }
        var role by remember { mutableStateOf(targetWorker.role.ifBlank { "Master Carpenter" }) }
        var dailyWage by remember { mutableStateOf(if (targetWorker.dailyWage > 0) targetWorker.dailyWage.toString() else "850") }
        var status by remember { mutableStateOf(targetWorker.status.ifBlank { "Active" }) }

        val roleOptions = listOf(
            "Master Carpenter",
            "SS Railing Fabricator",
            "Glass & Aluminium Installer",
            "UPVC Window Specialist",
            "Workshop Supervisor",
            "Helper"
        )

        AlertDialog(
            onDismissRequest = {
                showAddWorkerDialog = false
                editingWorker = null
            },
            title = {
                Text(
                    if (editingWorker != null)
                        (if (isHi) "कारीगर विवरण संपादित करें" else "Edit Worker Details")
                    else
                        (if (isHi) "नया कारीगर / स्टाफ जोड़ें" else "Add New Worker / Staff")
                )
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(if (isHi) "नाम (Full Name)" else "Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text(if (isHi) "मोबाइल नंबर (Mobile Phone)" else "Phone") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text(if (isHi) "भूमिका व विभाग (Role):" else "Role / Designation:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            roleOptions.take(3).forEach { opt ->
                                FilterChip(
                                    selected = role == opt,
                                    onClick = { role = opt },
                                    label = { Text(opt, fontSize = 10.sp) }
                                )
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            roleOptions.drop(3).forEach { opt ->
                                FilterChip(
                                    selected = role == opt,
                                    onClick = { role = opt },
                                    label = { Text(opt, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = dailyWage,
                            onValueChange = { dailyWage = it },
                            label = { Text(if (isHi) "दैनिक मजदूरी ₹ (Daily Wage)" else "Daily Wage (₹)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val wageVal = dailyWage.toDoubleOrNull() ?: 800.0
                        val updated = targetWorker.copy(
                            name = name,
                            phone = phone,
                            role = role,
                            dailyWage = wageVal,
                            status = status
                        )
                        viewModel.addOrUpdateWorker(updated) {
                            showAddWorkerDialog = false
                            editingWorker = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text(if (isHi) "सुरक्षित करें" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddWorkerDialog = false
                    editingWorker = null
                }) {
                    Text(if (isHi) "रद्द करें" else "Cancel")
                }
            }
        )
    }

    if (workerToDelete != null) {
        AlertDialog(
            onDismissRequest = { workerToDelete = null },
            title = { Text(if (isHi) "कारीगर को हटाएं?" else "Delete Worker?") },
            text = { Text("${workerToDelete?.name} को सूची से हटाना चाहते हैं?") },
            confirmButton = {
                Button(
                    onClick = {
                        workerToDelete?.id?.let { viewModel.deleteWorker(it) }
                        workerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                ) {
                    Text(if (isHi) "हटाएं" else "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { workerToDelete = null }) {
                    Text(if (isHi) "रद्द करें" else "Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isHi) "वर्कशॉप स्टाफ व कारीगर (${workers.size})" else "Workshop Workers & Staff (${workers.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Text(
                        text = if (isHi) "दैनिक हाजिरी, वेतन और भूमिका प्रबंधन" else "Manage roles, daily wage & status",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = { showAddWorkerDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_add_worker_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isHi) "नया जोड़ें" else "Add New", fontSize = 12.sp)
                }
            }
        }

        if (workers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isHi) "कोई कारीगर पंजीकृत नहीं है" else "No workers added yet",
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.reseedDemoData() },
                            colors = ButtonDefaults.buttonColors(containerColor = NavySecondary)
                        ) {
                            Text(if (isHi) "डेमो कारीगर लोड करें" else "Load Sample Workers")
                        }
                    }
                }
            }
        } else {
            items(workers) { worker ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(NavySecondary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = worker.name.take(1).uppercase(),
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = worker.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                Text(
                                    text = "${worker.role} • ${worker.phone}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }

                            Surface(
                                color = if (worker.status == "Active") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (worker.status == "Active") "सक्रिय (Active)" else "छुट्टी (Leave)",
                                    color = if (worker.status == "Active") GreenSuccess else RedDanger,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = SteelLight)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = if (isHi) "दैनिक वेतन (Wage)" else "Daily Wage",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "${formatInr(worker.dailyWage)} / दिन",
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary,
                                    fontSize = 13.sp
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconButton(
                                    onClick = { openDialer(context, worker.phone) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = "Call", tint = GreenSuccess, modifier = Modifier.size(18.dp))
                                }
                                IconButton(
                                    onClick = {
                                        openWhatsApp(
                                            context,
                                            worker.phone,
                                            "नमस्ते ${worker.name} जी, अरावल्ली स्टील व पीवीसी मोडासा से संपर्क किया जा रहा है।"
                                        )
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "WhatsApp", tint = NavySecondary, modifier = Modifier.size(18.dp))
                                }
                                IconButton(
                                    onClick = { editingWorker = worker },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NavyPrimary, modifier = Modifier.size(18.dp))
                                }
                                IconButton(
                                    onClick = { workerToDelete = worker },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedDanger, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: MASTER RATES & GST CONTROL
// -------------------------------------------------------------
@Composable
fun AdminRatesTab(
    viewModel: AravalliViewModel,
    products: List<Product>,
    settings: BusinessSettings?,
    isHi: Boolean
) {
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var selectedGst by remember { mutableStateOf(settings?.defaultGstRate ?: 18.0) }

    if (editingProduct != null) {
        val prod = editingProduct!!
        var sellingPrice by remember { mutableStateOf(prod.sellingPrice.toString()) }
        var costPrice by remember { mutableStateOf(prod.costPrice.toString()) }
        var stockQty by remember { mutableStateOf(prod.stockQuantity.toString()) }

        AlertDialog(
            onDismissRequest = { editingProduct = null },
            title = { Text(if (isHi) "दर व स्टॉक संपादित करें" else "Edit Rate & Stock") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = prod.name,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "इकाई (Unit): ${prod.unit} • श्रेणी: ${prod.category}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = sellingPrice,
                        onValueChange = { sellingPrice = it },
                        label = { Text(if (isHi) "बिक्री दर (Selling Rate ₹/${prod.unit})" else "Selling Price") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = costPrice,
                        onValueChange = { costPrice = it },
                        label = { Text(if (isHi) "लागत दर (Cost Price ₹/${prod.unit})" else "Cost Price") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = stockQty,
                        onValueChange = { stockQty = it },
                        label = { Text(if (isHi) "उपलब्ध स्टॉक (Stock Qty)" else "Stock Quantity") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val s = sellingPrice.toDoubleOrNull() ?: prod.sellingPrice
                        val c = costPrice.toDoubleOrNull() ?: prod.costPrice
                        val q = stockQty.toDoubleOrNull() ?: prod.stockQuantity
                        viewModel.updateProductPricing(prod.id, s, c, q)
                        editingProduct = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text(if (isHi) "अपडेट करें" else "Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingProduct = null }) {
                    Text(if (isHi) "रद्द करें" else "Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // GST Master Configuration Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AttachMoney, contentDescription = null, tint = NavyPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHi) "मास्टर जीएसटी दर (Default GST Rate)" else "Master GST Rate",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isHi) "सभी नए कोटेशन और अनुमानों पर यह जीएसटी दर लागू होगी:" else "Applies to all new estimates and billings:",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(18.0, 12.0, 5.0, 0.0).forEach { rate ->
                            FilterChip(
                                selected = selectedGst == rate,
                                onClick = {
                                    selectedGst = rate
                                    viewModel.updateBusinessGst(rate)
                                },
                                label = {
                                    Text(
                                        text = "${rate.toInt()}%",
                                        fontWeight = if (selectedGst == rate) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldAccent,
                                    selectedLabelColor = NavyPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Standard Advance Policy Note
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SteelLight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = NavyPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isHi) "अरावल्ली भुगतान नीति (Payment Terms)" else "Standard Payment Terms",
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (isHi) "• 50% एडवांस बुकिंग • 40% माल साइट पर आने पर • 10% फिनिशिंग के बाद" else "50% advance, 40% on material delivery, 10% on completion",
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }

        // Product Rate List
        item {
            Text(
                text = if (isHi) "उत्पाद एवं सेवा दर सूची (Master Rate Card)" else "Product & Rate Card",
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
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = prod.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            Text(
                                text = "${prod.category} • ${prod.sku.ifBlank { "ASPF" }}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        IconButton(
                            onClick = { editingProduct = prod },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NavyPrimary, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = SteelLight)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = if (isHi) "बिक्री दर" else "Selling Rate", fontSize = 10.sp, color = Color.Gray)
                            Text(
                                text = "${formatInr(prod.sellingPrice)} / ${prod.unit}",
                                fontWeight = FontWeight.Bold,
                                color = GreenSuccess,
                                fontSize = 13.sp
                            )
                        }

                        Column {
                            Text(text = if (isHi) "लागत दर" else "Cost Price", fontSize = 10.sp, color = Color.Gray)
                            Text(
                                text = "${formatInr(prod.costPrice)} / ${prod.unit}",
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray,
                                fontSize = 13.sp
                            )
                        }

                        Column {
                            Text(text = if (isHi) "स्टॉक" else "In Stock", fontSize = 10.sp, color = Color.Gray)
                            Text(
                                text = "${prod.stockQuantity.toInt()} ${prod.unit}",
                                fontWeight = FontWeight.Bold,
                                color = if (prod.stockQuantity <= 50) OrangeWarning else NavyPrimary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: ORDER PRODUCTION & APPROVAL CONTROLS
// -------------------------------------------------------------
@Composable
fun AdminOrdersTab(
    viewModel: AravalliViewModel,
    orders: List<Order>,
    isHi: Boolean
) {
    val context = LocalContext.current
    var statusFilter by remember { mutableStateOf("All") }
    var orderToDelete by remember { mutableStateOf<Order?>(null) }

    val statusOptions = listOf("All", "Confirmed", "Material Purchased", "Manufacturing", "Installation", "Completed")

    val filteredOrders = if (statusFilter == "All") {
        orders
    } else {
        orders.filter { it.status.equals(statusFilter, ignoreCase = true) }
    }

    if (orderToDelete != null) {
        AlertDialog(
            onDismissRequest = { orderToDelete = null },
            title = { Text(if (isHi) "ऑर्डर हटाएं?" else "Delete Order?") },
            text = { Text("ऑर्डर #${orderToDelete?.orderNumber} को हटाना चाहते हैं?") },
            confirmButton = {
                Button(
                    onClick = {
                        orderToDelete?.id?.let { viewModel.deleteOrder(it) }
                        orderToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                ) {
                    Text(if (isHi) "हटाएं" else "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToDelete = null }) {
                    Text(if (isHi) "रद्द करें" else "Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = if (isHi) "ऑर्डर निर्माण व डिलीवरी नियंत्रण (${orders.size})" else "Order Dispatch & Approvals (${orders.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                statusOptions.take(3).forEach { st ->
                    FilterChip(
                        selected = statusFilter == st,
                        onClick = { statusFilter = st },
                        label = { Text(st, fontSize = 10.sp) }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                statusOptions.drop(3).forEach { st ->
                    FilterChip(
                        selected = statusFilter == st,
                        onClick = { statusFilter = st },
                        label = { Text(st, fontSize = 10.sp) }
                    )
                }
            }
        }

        if (filteredOrders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isHi) "इस श्रेणी में कोई ऑर्डर नहीं है" else "No orders found in this filter",
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(filteredOrders) { order ->
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
                                    text = "#${order.orderNumber} • ${order.customerName}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                Text(
                                    text = "${order.projectType} • ${order.customerPhone}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            Surface(
                                color = when (order.status) {
                                    "Completed" -> Color(0xFFE8F5E9)
                                    "Manufacturing", "Installation" -> Color(0xFFFFF3E0)
                                    else -> SteelLight
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = order.status,
                                    color = when (order.status) {
                                        "Completed" -> GreenSuccess
                                        "Manufacturing", "Installation" -> OrangeWarning
                                        else -> NavyPrimary
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Amounts
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "कुल: ${formatInr(order.totalAmount)}",
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "प्राप्त: ${formatInr(order.advancePaid)}",
                                fontSize = 11.sp,
                                color = GreenSuccess,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "बकाया: ${formatInr(order.balanceAmount)}",
                                fontSize = 11.sp,
                                color = RedDanger,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = SteelLight)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Status Update Actions
                        Text(
                            text = if (isHi) "स्थिति बदलें (Update Status):" else "Change Status:",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (order.status != "Manufacturing") {
                                OutlinedButton(
                                    onClick = { viewModel.updateOrderStatus(order.id, "Manufacturing") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("निर्माण", fontSize = 10.sp)
                                }
                            }
                            if (order.status != "Installation") {
                                OutlinedButton(
                                    onClick = { viewModel.updateOrderStatus(order.id, "Installation") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("साइट", fontSize = 10.sp)
                                }
                            }
                            if (order.status != "Completed") {
                                Button(
                                    onClick = { viewModel.updateOrderStatus(order.id, "Completed") },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("पूर्ण", fontSize = 10.sp, color = Color.White)
                                }
                            }
                            IconButton(
                                onClick = {
                                    openWhatsApp(
                                        context,
                                        order.customerPhone,
                                        "नमस्ते ${order.customerName} जी, अरावल्ली स्टील व पीवीसी से आपके ऑर्डर #${order.orderNumber} (${order.projectType}) की वर्तमान स्थिति: ${order.status} है। धन्यवाद।"
                                    )
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "WhatsApp", tint = NavySecondary, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = { orderToDelete = order },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedDanger, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: SHOP PROFILE & MASTER SETTINGS
// -------------------------------------------------------------
@Composable
fun AdminSettingsTab(
    viewModel: AravalliViewModel,
    settings: BusinessSettings?,
    isHi: Boolean
) {
    val current = settings ?: BusinessSettings()

    var businessName by remember { mutableStateOf(current.businessName) }
    var phone by remember { mutableStateOf(current.phone) }
    var whatsapp by remember { mutableStateOf(current.whatsapp) }
    var email by remember { mutableStateOf(current.email) }
    var address by remember { mutableStateOf(current.address) }
    var gstin by remember { mutableStateOf(current.gstin) }
    var terms by remember { mutableStateOf(current.estimateTerms) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = NavyPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHi) "दुकान व फर्म विवरण (Business Profile)" else "Business & Shop Identity",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text(if (isHi) "फर्म का नाम (Shop Name)" else "Business Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(if (isHi) "संपर्क फोन (Phone)" else "Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text(if (isHi) "व्हाट्सएप नंबर (WhatsApp)" else "WhatsApp") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = gstin,
                        onValueChange = { gstin = it },
                        label = { Text(if (isHi) "जीएसटी नंबर (GSTIN)" else "GSTIN") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text(if (isHi) "दुकान का पूरा पता (Address)" else "Address") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = terms,
                        onValueChange = { terms = it },
                        label = { Text(if (isHi) "कोटेशन नियम व शर्तें (Quotation Terms)" else "Terms & Conditions") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val updated = current.copy(
                                businessName = businessName,
                                phone = phone,
                                whatsapp = whatsapp,
                                email = email,
                                address = address,
                                gstin = gstin,
                                estimateTerms = terms
                            )
                            viewModel.saveSettings(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isHi) "सभी सेटिंग्स सुरक्षित करें" else "Save All Settings", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

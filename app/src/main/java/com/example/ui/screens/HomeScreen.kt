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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.KpiCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatInr
import com.example.ui.components.openWhatsApp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.OrangeWarning
import com.example.ui.viewmodel.AravalliViewModel

@Composable
fun HomeScreen(
    viewModel: AravalliViewModel,
    onNavigateToEstimates: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToCustomers: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToTools: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToCatalogue: () -> Unit,
    onNavigateToAiTools: () -> Unit = {},
    onNavigateToAdmin: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val workers by viewModel.workers.collectAsState()
    val products by viewModel.products.collectAsState()
    val estimates by viewModel.estimates.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    val todaySales = viewModel.getTodaySales()
    val monthlySales = viewModel.getMonthlySales()
    val totalExpenses = viewModel.getTotalExpenses()
    val outstanding = viewModel.getOutstandingReceivables()
    val netProfit = (monthlySales - totalExpenses).coerceAtLeast(0.0)

    val pendingOrders = orders.filter { it.status != "Completed" && it.status != "Cancelled" }
    val completedOrders = orders.filter { it.status == "Completed" }

    val isHi = currentLang == "hi"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen")
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Business Brand Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("business_banner_card"),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ARAVALLI STEEL PVC FURNITURE",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Shyam Arcade, Meghraj Road, Modasa • 383315",
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldAccent
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(GoldAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AS", fontWeight = FontWeight.Black, color = NavyPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isHi) "इस महीने की बिक्री (Sales)" else "Monthly Sales",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.LightGray
                            )
                            Text(
                                text = formatInr(monthlySales),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isHi) "अनुमानित लाभ (Net Profit)" else "Estimated Profit",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.LightGray
                            )
                            Text(
                                text = formatInr(netProfit),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF86EFAC)
                            )
                        }
                    }
                }
            }
        }

        // Quick Action Buttons
        item {
            Text(
                text = if (isHi) "त्वरित क्रियाएँ (Quick Actions)" else "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    QuickActionButton(
                        label = if (isHi) "🛡️ एडमिन पैनल" else "🛡️ Admin Panel",
                        icon = Icons.Default.AdminPanelSettings,
                        color = NavyPrimary,
                        onClick = onNavigateToAdmin
                    )
                }
                item {
                    QuickActionButton(
                        label = if (isHi) "+ नया कोटेशन" else "+ New Quote",
                        icon = Icons.Default.Calculate,
                        color = NavyPrimary,
                        onClick = onNavigateToTools
                    )
                }
                item {
                    QuickActionButton(
                        label = if (isHi) "✨ जेमिनी AI" else "✨ Gemini AI",
                        icon = Icons.Default.AutoAwesome,
                        color = Color(0xFF6D28D9),
                        onClick = onNavigateToAiTools
                    )
                }
                item {
                    QuickActionButton(
                        label = if (isHi) "+ नया ग्राहक" else "+ Add Lead",
                        icon = Icons.Default.Group,
                        color = NavySecondary,
                        onClick = onNavigateToCustomers
                    )
                }
                item {
                    QuickActionButton(
                        label = if (isHi) "कारीगर हाजिरी" else "Attendance",
                        icon = Icons.Default.Engineering,
                        color = Color(0xFFD97706),
                        onClick = onNavigateToAttendance
                    )
                }
                item {
                    QuickActionButton(
                        label = if (isHi) "+ उत्पाद जोड़ें" else "+ Add Product",
                        icon = Icons.Default.Inventory,
                        color = Color(0xFF0D9488),
                        onClick = onNavigateToProducts
                    )
                }
                item {
                    QuickActionButton(
                        label = if (isHi) "कैटलॉग शेयर" else "Share Catalogue",
                        icon = Icons.Default.Share,
                        color = GreenSuccess,
                        onClick = {
                            openWhatsApp(
                                context,
                                "",
                                "नमस्ते! अरावल्ली स्टील पीवीसी फर्नीचर (मोडासा, गुजरात) के प्रीमियम एसएस रेलिंग, मॉड्यूलर किचन, और पीवीसी फर्नीचर कैटलॉग देखने के लिए संपर्क करें: +91 98250 12345"
                            )
                        }
                    )
                }
            }
        }

        // Financial KPIs Grid
        item {
            Text(
                text = if (isHi) "वित्तीय सारांश (Financial Summary)" else "Financial Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    title = if (isHi) "आज की आवक (Today)" else "Today's Income",
                    value = formatInr(todaySales),
                    icon = Icons.Default.MonetizationOn,
                    iconBgColor = GreenSuccess,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = if (isHi) "बाकी वसूली (Pending)" else "Receivables",
                    value = formatInr(outstanding),
                    icon = Icons.Default.PendingActions,
                    iconBgColor = OrangeWarning,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    title = if (isHi) "लंबित ऑर्डर" else "Pending Orders",
                    value = "${pendingOrders.size} ऑर्डर्स",
                    icon = Icons.Default.Assignment,
                    iconBgColor = NavyPrimary,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = if (isHi) "सक्रिय कारीगर" else "Active Workers",
                    value = "${workers.size} कारीगर",
                    icon = Icons.Default.Engineering,
                    iconBgColor = NavySecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Recent Orders Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHi) "हाल के ऑर्डर्स (Recent Orders)" else "Recent Orders",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Text(
                    text = if (isHi) "सभी देखें →" else "View All →",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavySecondary,
                    modifier = Modifier.clickable { onNavigateToOrders() }
                )
            }
        }

        if (orders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isHi) "अभी कोई ऑर्डर नहीं है। नए कोटेशन को ऑर्डर में बदलें!" else "No orders yet. Convert quotes to orders!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(orders.take(4)) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToOrders() },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = order.customerName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusBadge(status = order.status)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${order.projectType} • डिलीवरी: ${order.deliveryDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatInr(order.totalAmount),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            if (order.balanceAmount > 0) {
                                Text(
                                    text = "बाकी: ${formatInr(order.balanceAmount)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFDC2626)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = color,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

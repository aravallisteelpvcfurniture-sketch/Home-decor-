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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.Transaction
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatInr
import com.example.ui.components.openDialer
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SteelLight
import com.example.ui.viewmodel.AravalliViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(viewModel: AravalliViewModel) {
    val context = LocalContext.current
    val orders by viewModel.orders.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isHi = currentLang == "hi"

    var selectedStatusFilter by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedOrderForStatusUpdate by remember { mutableStateOf<Order?>(null) }
    var selectedOrderForPayment by remember { mutableStateOf<Order?>(null) }

    val statusOptions = listOf(
        "All",
        "Confirmed",
        "Material Purchased",
        "Manufacturing",
        "Installation",
        "Completed"
    )

    val filteredOrders = orders.filter {
        selectedStatusFilter == "All" || it.status == selectedStatusFilter
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = NavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("new_order_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Order")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Status Filter Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statusOptions) { status ->
                    val isSelected = selectedStatusFilter == status
                    Surface(
                        modifier = Modifier.clickable { selectedStatusFilter = status },
                        color = if (isSelected) NavyPrimary else SteelLight,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = status,
                            color = if (isSelected) Color.White else Color.Black,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            if (filteredOrders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Assignment, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isHi) "इस स्थिति में कोई ऑर्डर नहीं है" else "No orders in this status",
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders, key = { it.id.ifBlank { it.orderNumber } }) { order ->
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
                                            text = "#${order.orderNumber}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = NavySecondary
                                        )
                                        Text(
                                            text = order.customerName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyPrimary
                                        )
                                    }
                                    StatusBadge(status = order.status)
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "प्रोजेक्ट: ${order.projectType} • डिलीवरी: ${order.deliveryDate}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.DarkGray
                                )
                                Text(
                                    text = "जिम्मेदार कारीगर: ${order.assignedWorker.ifBlank { "Team Modasa" }}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Divider(color = SteelLight)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("कुल: ${formatInr(order.totalAmount)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        Row {
                                            Text("एडवांस: ${formatInr(order.advancePaid)}  |  ", style = MaterialTheme.typography.bodySmall, color = Color(0xFF15803D))
                                            Text("बाकी: ${formatInr(order.balanceAmount)}", style = MaterialTheme.typography.bodySmall, color = if (order.balanceAmount > 0) Color(0xFFDC2626) else Color.Gray)
                                        }
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        if (order.customerPhone.isNotBlank()) {
                                            IconButton(onClick = { openDialer(context, order.customerPhone) }) {
                                                Icon(Icons.Default.Phone, contentDescription = "Call", tint = NavySecondary)
                                            }
                                        }

                                        if (order.balanceAmount > 0) {
                                            IconButton(onClick = { selectedOrderForPayment = order }) {
                                                Icon(Icons.Default.Payment, contentDescription = "Pay", tint = Color(0xFF15803D))
                                            }
                                        }

                                        Button(
                                            onClick = { selectedOrderForStatusUpdate = order },
                                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("स्थिति (Status)", fontSize = 11.sp)
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

    // Status Update Dialog
    selectedOrderForStatusUpdate?.let { ord ->
        val statusList = listOf("Confirmed", "Material Purchased", "Manufacturing", "Installation", "Completed", "Cancelled")
        AlertDialog(
            onDismissRequest = { selectedOrderForStatusUpdate = null },
            title = { Text("ऑर्डर स्थिति बदलें (${ord.orderNumber})", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    statusList.forEach { s ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateOrderStatus(ord.id, s)
                                    selectedOrderForStatusUpdate = null
                                }
                                .padding(vertical = 4.dp),
                            color = if (ord.status == s) NavyPrimary else SteelLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = s,
                                color = if (ord.status == s) Color.White else Color.Black,
                                modifier = Modifier.padding(12.dp),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedOrderForStatusUpdate = null }) {
                    Text("रद्द करें (Cancel)")
                }
            }
        )
    }

    // Payment Dialog
    selectedOrderForPayment?.let { ord ->
        var paymentAmtStr by remember { mutableStateOf(ord.balanceAmount.toInt().toString()) }
        var paymentMode by remember { mutableStateOf("UPI") }

        AlertDialog(
            onDismissRequest = { selectedOrderForPayment = null },
            title = { Text("भुगतान प्राप्त करें (Receive Payment)", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("ग्राहक: ${ord.customerName} (#${ord.orderNumber})", fontWeight = FontWeight.Bold)
                    Text("बाकी राशि: ${formatInr(ord.balanceAmount)}")

                    OutlinedTextField(
                        value = paymentAmtStr,
                        onValueChange = { paymentAmtStr = it },
                        label = { Text("प्राप्त राशि (Received ₹)*") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("UPI", "Cash", "Bank").forEach { mode ->
                            val isSel = paymentMode == mode
                            Surface(
                                modifier = Modifier.clickable { paymentMode = mode },
                                color = if (isSel) NavyPrimary else SteelLight,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = mode,
                                    color = if (isSel) Color.White else Color.Black,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val paid = paymentAmtStr.toDoubleOrNull() ?: 0.0
                        if (paid > 0) {
                            val newAdvance = ord.advancePaid + paid
                            val newBalance = (ord.totalAmount - newAdvance).coerceAtLeast(0.0)
                            val updatedOrder = ord.copy(advancePaid = newAdvance, balanceAmount = newBalance)
                            viewModel.addOrUpdateOrder(updatedOrder)

                            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                            viewModel.addTransaction(
                                Transaction(
                                    type = "Income",
                                    category = "Customer Balance",
                                    amount = paid,
                                    paymentMode = paymentMode,
                                    reference = ord.orderNumber,
                                    date = today,
                                    notes = "Payment from ${ord.customerName} for #${ord.orderNumber}"
                                )
                            )
                            selectedOrderForPayment = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
                ) {
                    Text("भुगतान दर्ज करें (Record Payment)")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedOrderForPayment = null }) { Text("रद्द करें") }
            }
        )
    }

    if (showAddDialog) {
        CreateOrderDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { newOrd ->
                viewModel.addOrUpdateOrder(newOrd) {
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
fun CreateOrderDialog(
    onDismiss: () -> Unit,
    onConfirm: (Order) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var projectType by remember { mutableStateOf("SS Railing") }
    var totalAmountStr by remember { mutableStateOf("") }
    var advancePaidStr by remember { mutableStateOf("") }
    var deliveryDate by remember { mutableStateOf("15 Days") }
    var worker by remember { mutableStateOf("Team Modasa") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("नया कार्य आदेश (New Order)", fontWeight = FontWeight.Bold) },
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
                        label = { Text("प्रोजेक्ट (SS Railing, Kitchen, Wardrobe)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = totalAmountStr,
                            onValueChange = { totalAmountStr = it },
                            label = { Text("कुल राशि (Total ₹)*") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = advancePaidStr,
                            onValueChange = { advancePaidStr = it },
                            label = { Text("एडवांस मिला (Advance ₹)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = deliveryDate,
                        onValueChange = { deliveryDate = it },
                        label = { Text("प्रस्तावित डिलीवरी (Promised Delivery)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = worker,
                        onValueChange = { worker = it },
                        label = { Text("जिम्मेदार कारीगर (Assigned Worker)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val total = totalAmountStr.toDoubleOrNull() ?: 0.0
                    val advance = advancePaidStr.toDoubleOrNull() ?: 0.0
                    val balance = (total - advance).coerceAtLeast(0.0)
                    val ordNum = "ORD-" + System.currentTimeMillis().toString().takeLast(6)

                    if (customerName.isNotBlank() && total > 0) {
                        onConfirm(
                            Order(
                                orderNumber = ordNum,
                                customerName = customerName.trim(),
                                customerPhone = customerPhone.trim(),
                                projectType = projectType.trim(),
                                totalAmount = total,
                                advancePaid = advance,
                                balanceAmount = balance,
                                deliveryDate = deliveryDate.trim(),
                                assignedWorker = worker.trim(),
                                status = "Confirmed"
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

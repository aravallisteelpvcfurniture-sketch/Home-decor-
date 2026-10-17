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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Transaction
import com.example.ui.components.formatInr
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.RedDanger
import com.example.ui.theme.SteelLight
import com.example.ui.viewmodel.AravalliViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FinanceScreen(viewModel: AravalliViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isHi = currentLang == "hi"

    var showAddDialog by remember { mutableStateOf(false) }

    val totalIncome = transactions.filter { it.type == "Income" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "Expense" }.sumOf { it.amount }
    val netCashBalance = totalIncome - totalExpense

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = NavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_transaction_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Entry")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Cash Balance Summary Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "दुकान रोकड़ बही (Shop Cashbook Balance)",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatInr(netCashBalance),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (netCashBalance >= 0) Color(0xFF86EFAC) else Color(0xFFFCA5A5)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("कुल आवक (Income)", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                                }
                                Text(formatInr(totalIncome), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = RedDanger, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("कुल खर्च (Expense)", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                                }
                                Text(formatInr(totalExpense), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = if (isHi) "लेन-देन प्रविष्टियां (Transactions Register)" else "Recent Entries",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            }

            if (transactions.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("अभी कोई लेन-देन प्रविष्टि नहीं है।", color = Color.Gray)
                    }
                }
            } else {
                items(transactions.reversed(), key = { it.id.ifBlank { "${it.date}_${it.amount}_${it.reference}" } }) { tx ->
                    val isIncome = tx.type == "Income"
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(if (isIncome) Color(0xFFDCFCE7) else Color(0xFFFEE2E2), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = if (isIncome) GreenSuccess else RedDanger,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.category,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                Text(
                                    text = "${tx.date} • ${tx.paymentMode} ${if (tx.reference.isNotBlank()) "• Ref: ${tx.reference}" else ""}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                                if (tx.notes.isNotBlank()) {
                                    Text(
                                        text = tx.notes,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.DarkGray
                                    )
                                }
                            }

                            Text(
                                text = (if (isIncome) "+ " else "- ") + formatInr(tx.amount),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isIncome) GreenSuccess else RedDanger
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTransactionDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { newTx ->
                viewModel.addTransaction(newTx) {
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (Transaction) -> Unit
) {
    var type by remember { mutableStateOf("Income") }
    var category by remember { mutableStateOf("Customer Advance") }
    var amountStr by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf("Cash") }
    var reference by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val incomeCategories = listOf("Customer Advance", "Customer Balance", "Other Income")
    val expenseCategories = listOf("Material Purchase", "Worker Wage", "Worker Advance", "Shop Rent", "Electricity", "Transport", "Other Expense")

    val categories = if (type == "Income") incomeCategories else expenseCategories

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("खाता प्रविष्टि जोड़ें (Add Transaction)", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                type = "Income"
                                category = "Customer Advance"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (type == "Income") GreenSuccess else SteelLight),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("आवक (Income)", color = if (type == "Income") Color.White else Color.Black)
                        }
                        Button(
                            onClick = {
                                type = "Expense"
                                category = "Material Purchase"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (type == "Expense") RedDanger else SteelLight),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("खर्च (Expense)", color = if (type == "Expense") Color.White else Color.Black)
                        }
                    }
                }
                item {
                    Text("श्रेणी (Category):", style = MaterialTheme.typography.labelMedium)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        categories.forEach { cat ->
                            val isSel = category == cat
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { category = cat },
                                color = if (isSel) NavyPrimary else SteelLight,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSel) Color.White else Color.Black,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("राशि (Amount ₹)*") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text("भुगतान मोड (Payment Mode):", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Cash", "UPI", "Bank Transfer").forEach { mode ->
                            val isSel = paymentMode == mode
                            Surface(
                                modifier = Modifier.clickable { paymentMode = mode },
                                color = if (isSel) NavyPrimary else SteelLight,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = mode,
                                    color = if (isSel) Color.White else Color.Black,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = reference,
                        onValueChange = { reference = it },
                        label = { Text("बिल / संदर्भ संख्या (Bill / Ref No.)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("विवरण / टिप्पणी (Notes)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    if (amt > 0) {
                        onConfirm(
                            Transaction(
                                type = type,
                                category = category,
                                amount = amt,
                                paymentMode = paymentMode,
                                reference = reference.trim(),
                                date = today,
                                notes = notes.trim()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("सुरक्षित करें")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("रद्द करें") }
        }
    )
}

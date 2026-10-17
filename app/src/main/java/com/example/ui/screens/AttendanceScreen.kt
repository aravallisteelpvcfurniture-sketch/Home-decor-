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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Attendance
import com.example.data.model.Transaction
import com.example.data.model.Worker
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatInr
import com.example.ui.components.openWhatsApp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.OrangeWarning
import com.example.ui.theme.RedDanger
import com.example.ui.theme.SteelLight
import com.example.ui.viewmodel.AravalliViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AttendanceScreen(viewModel: AravalliViewModel) {
    val context = LocalContext.current
    val workers by viewModel.workers.collectAsState()
    val attendanceList by viewModel.attendance.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isHi = currentLang == "hi"

    val todayDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var selectedDate by remember { mutableStateOf(todayDate) }
    var showAddWorkerDialog by remember { mutableStateOf(false) }
    var selectedWorkerForAttendance by remember { mutableStateOf<Worker?>(null) }

    val todayAttendanceMap = attendanceList
        .filter { it.date == selectedDate }
        .associateBy { it.workerId }

    val totalWageToday = todayAttendanceMap.values.sumOf { it.dailyEarned }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddWorkerDialog = true },
                containerColor = NavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_worker_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Worker")
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
            // Date Header & Daily Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "तारीख: $selectedDate",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Button(
                                onClick = {
                                    val msg = "अरावल्ली स्टील पीवीसी फर्नीचर (मोडासा):\n" +
                                            "कारीगर दैनिक हाजिरी सारांश ($selectedDate):\n" +
                                            "कुल सक्रिय कारीगर: ${workers.size}\n" +
                                            "उपस्थित: ${todayAttendanceMap.values.count { it.status == "Present" }}\n" +
                                            "कुल दैनिक मजदूरी: ${formatInr(totalWageToday)}"
                                    openWhatsApp(context, "", msg)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NavySecondary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("शेयर", fontSize = 11.sp, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("आज की कुल मजदूरी (Total Wages):", style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
                            Text(formatInr(totalWageToday), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF86EFAC))
                        }
                    }
                }
            }

            // Workers List with Attendance State
            item {
                Text(
                    text = if (isHi) "कारीगर एवं हाजिरी रजिस्टर (Workers Register)" else "Workers Attendance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            }

            if (workers.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("कोई कारीगर प्रोफ़ाइल नहीं मिली।", color = Color.Gray)
                    }
                }
            } else {
                items(workers, key = { it.id.ifBlank { it.name } }) { worker ->
                    val record = todayAttendanceMap[worker.id]
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
                                        text = worker.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary
                                    )
                                    Text(
                                        text = "${worker.role} • दर: ${formatInr(worker.dailyWage)}/दिन",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }

                                if (record != null) {
                                    StatusBadge(status = record.status)
                                } else {
                                    Surface(
                                        color = Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "लंबित (Not Marked)",
                                            color = Color(0xFFB45309),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (record != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "दैनिक मजदूरी: ${formatInr(record.dailyEarned)}" + if (record.advanceGiven > 0) " (एडवांस: ${formatInr(record.advanceGiven)})" else "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF15803D),
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Button(
                                        onClick = { selectedWorkerForAttendance = worker },
                                        colors = ButtonDefaults.buttonColors(containerColor = NavySecondary),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("बदलें (Edit)", fontSize = 11.sp)
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AttendanceQuickButton("उपस्थित (Present)", GreenSuccess, Modifier.weight(1f)) {
                                        val rec = Attendance(
                                            workerId = worker.id,
                                            workerName = worker.name,
                                            date = selectedDate,
                                            status = "Present",
                                            dailyEarned = worker.dailyWage
                                        )
                                        viewModel.recordAttendance(rec)
                                    }

                                    AttendanceQuickButton("आधा दिन (Half)", OrangeWarning, Modifier.weight(1f)) {
                                        val rec = Attendance(
                                            workerId = worker.id,
                                            workerName = worker.name,
                                            date = selectedDate,
                                            status = "Half Day",
                                            dailyEarned = worker.dailyWage * 0.5
                                        )
                                        viewModel.recordAttendance(rec)
                                    }

                                    AttendanceQuickButton("अनुपस्थित", RedDanger, Modifier.weight(1f)) {
                                        val rec = Attendance(
                                            workerId = worker.id,
                                            workerName = worker.name,
                                            date = selectedDate,
                                            status = "Absent",
                                            dailyEarned = 0.0
                                        )
                                        viewModel.recordAttendance(rec)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Attendance Dialog with Overtime and Advance
    selectedWorkerForAttendance?.let { worker ->
        var status by remember { mutableStateOf("Present") }
        var overtimeHoursStr by remember { mutableStateOf("0") }
        var advanceGivenStr by remember { mutableStateOf("0") }

        AlertDialog(
            onDismissRequest = { selectedWorkerForAttendance = null },
            title = { Text("हाजिरी विवरण: ${worker.name}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("दैनिक दर: ${formatInr(worker.dailyWage)}/दिन")

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Present", "Half Day", "Absent", "Leave").forEach { s ->
                            val isSel = status == s
                            Surface(
                                modifier = Modifier.clickable { status = s },
                                color = if (isSel) NavyPrimary else SteelLight,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = s,
                                    color = if (isSel) Color.White else Color.Black,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = overtimeHoursStr,
                        onValueChange = { overtimeHoursStr = it },
                        label = { Text("ओवरटाइम घंटे (Overtime Hours)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = advanceGivenStr,
                        onValueChange = { advanceGivenStr = it },
                        label = { Text("दैनिक एडवांस दिया (Advance Given ₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val ot = overtimeHoursStr.toDoubleOrNull() ?: 0.0
                        val adv = advanceGivenStr.toDoubleOrNull() ?: 0.0
                        val hourlyRate = worker.dailyWage / 8.0
                        val baseEarned = when (status) {
                            "Present" -> worker.dailyWage
                            "Half Day" -> worker.dailyWage * 0.5
                            else -> 0.0
                        }
                        val totalEarned = baseEarned + (ot * hourlyRate)

                        val rec = Attendance(
                            workerId = worker.id,
                            workerName = worker.name,
                            date = selectedDate,
                            status = status,
                            overtimeHours = ot,
                            advanceGiven = adv,
                            dailyEarned = totalEarned
                        )
                        viewModel.recordAttendance(rec)

                        if (adv > 0) {
                            viewModel.addTransaction(
                                Transaction(
                                    type = "Expense",
                                    category = "Worker Advance",
                                    amount = adv,
                                    paymentMode = "Cash",
                                    reference = worker.name,
                                    date = selectedDate,
                                    notes = "Advance to ${worker.name}"
                                )
                            )
                        }
                        selectedWorkerForAttendance = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("सुरक्षित करें")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedWorkerForAttendance = null }) { Text("रद्द करें") }
            }
        )
    }

    if (showAddWorkerDialog) {
        AddWorkerDialog(
            onDismiss = { showAddWorkerDialog = false },
            onConfirm = { newWorker ->
                viewModel.addOrUpdateWorker(newWorker) {
                    showAddWorkerDialog = false
                }
            }
        )
    }
}

@Composable
fun AttendanceQuickButton(text: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = color,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
            Text(text = text, color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AddWorkerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Worker) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Carpenter") }
    var wageStr by remember { mutableStateOf("800") }

    val roles = listOf("Master Carpenter", "SS Railing Fabricator", "Glass & Aluminium Installer", "Helper")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("नया कारीगर प्रोफ़ाइल (Add Worker)", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("कारीगर का नाम (Name)*") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("फोन नंबर (Phone)*") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("पद / हुनर (Role: Carpenter, Fabricator)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = wageStr,
                    onValueChange = { wageStr = it },
                    label = { Text("दैनिक मजदूरी दर (Daily Wage ₹)*") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val wage = wageStr.toDoubleOrNull() ?: 800.0
                    if (name.isNotBlank()) {
                        onConfirm(
                            Worker(
                                name = name.trim(),
                                phone = phone.trim(),
                                role = role.trim(),
                                dailyWage = wage,
                                status = "Active"
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

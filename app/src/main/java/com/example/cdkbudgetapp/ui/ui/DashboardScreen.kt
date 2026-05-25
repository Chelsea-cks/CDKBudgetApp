package com.example.cdkbudgetapp.ui.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cdkbudgetapp.navigation.Routes
import com.example.cdkbudgetapp.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    navController: NavController,
    darkMode: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    vm: TransactionViewModel = viewModel()
) {
    val list by vm.transactions.collectAsState()
    val streak by vm.streak.collectAsState()
    val minGoal by vm.minGoal.collectAsState()
    val maxGoal by vm.maxGoal.collectAsState()
    
    var selectedPeriod by remember { mutableStateOf("All Time") }
    val periods = listOf("Today", "This Week", "This Month", "All Time")
    var periodExpanded by remember { mutableStateOf(false) }

    val filteredList = vm.getFilteredTransactions(selectedPeriod, list)
    val totalSpentInPeriod = filteredList.sumOf { it.amount }

    // Form states
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("10:00") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
    }

    Column(Modifier.padding(16.dp)) {
        Spacer(Modifier.height(40.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("CDK BUDGET", style = MaterialTheme.typography.headlineLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Streak",
                        tint = Color(0xFFFF9800)
                    )
                    Text(" $streak Day Streak!", style = MaterialTheme.typography.titleMedium)
                }
            }
            Switch(checked = darkMode, onCheckedChange = onToggleTheme)
        }

        Spacer(Modifier.height(16.dp))

        // Goal Performance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Monthly Goal Performance", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                
                val progress = if (maxGoal > 0) (totalSpentInPeriod / maxGoal).toFloat().coerceIn(0f, 1f) else 0f
                val statusColor = when {
                    totalSpentInPeriod > maxGoal -> Color.Red
                    totalSpentInPeriod < minGoal -> Color.Yellow
                    else -> Color.Green
                }
                
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(12.dp),
                    color = statusColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Spent: R${"%.2f".format(totalSpentInPeriod)}", style = MaterialTheme.typography.bodySmall)
                    Text("Max Goal: R$maxGoal", style = MaterialTheme.typography.bodySmall)
                }
                
                val message = when {
                    totalSpentInPeriod > maxGoal -> "Over Budget! ⚠️"
                    totalSpentInPeriod >= minGoal -> "Target Zone! ✅"
                    else -> "Under Minimum Goal 📊"
                }
                Text(message, style = MaterialTheme.typography.labelMedium, color = statusColor)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Add Expense Section
        Text("Log Expense", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.weight(1f))
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date (YYYY-MM-DD)") }, modifier = Modifier.weight(1.2f))
            OutlinedTextField(value = startTime, onValueChange = { startTime = it }, label = { Text("Start") }, modifier = Modifier.weight(0.9f))
            OutlinedTextField(value = endTime, onValueChange = { endTime = it }, label = { Text("End") }, modifier = Modifier.weight(0.9f))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { photoPickerLauncher.launch("image/*") }) {
                Icon(Icons.Default.Add, "Add Photo")
            }
            if (photoUri != null) {
                Text("Photo attached ✅", style = MaterialTheme.typography.bodySmall)
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (description.isNotEmpty() && amount.isNotEmpty()) {
                        vm.add(description, amount.toDoubleOrNull() ?: 0.0, category, date, startTime, endTime, photoUri?.toString())
                        description = ""
                        amount = ""
                        category = ""
                        photoUri = null
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("Add") }

            Button(onClick = { navController.navigate(Routes.Chart.route) }, modifier = Modifier.weight(1f)) {
                Text("View Chart 📊")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Period filter for list
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Expenses for: ", style = MaterialTheme.typography.titleSmall)
            Box {
                TextButton(onClick = { periodExpanded = true }) {
                    Text(selectedPeriod)
                    Icon(Icons.Default.ArrowDropDown, null)
                }
                DropdownMenu(expanded = periodExpanded, onDismissRequest = { periodExpanded = false }) {
                    periods.forEach { p ->
                        DropdownMenuItem(text = { Text(p) }, onClick = { selectedPeriod = p; periodExpanded = false })
                    }
                }
            }
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredList) { item ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.description, style = MaterialTheme.typography.bodyLarge)
                                Text("${item.category} | ${item.date}", style = MaterialTheme.typography.bodySmall)
                                Text("${item.startTime} - ${item.endTime}", style = MaterialTheme.typography.bodySmall)
                            }
                            Text("R${item.amount}", style = MaterialTheme.typography.bodyLarge)
                            IconButton(onClick = { vm.delete(item) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                        if (item.photoUri != null) {
                            Spacer(Modifier.height(8.dp))
                            AsyncImage(
                                model = item.photoUri,
                                contentDescription = "Expense Photo",
                                modifier = Modifier.fillMaxWidth().height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}


package com.example.cdkbudgetapp.ui.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cdkbudgetapp.viewmodel.TransactionViewModel

@Composable
fun SettingsScreen(
    darkMode: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    vm: TransactionViewModel = viewModel()
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val minGoal by vm.minGoal.collectAsState()
    val maxGoal by vm.maxGoal.collectAsState()

    var minGoalText by remember { mutableStateOf(minGoal.toString()) }
    var maxGoalText by remember { mutableStateOf(maxGoal.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("Settings", style = MaterialTheme.typography.headlineLarge)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Goals Configuration
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Monthly Budget Goals", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = minGoalText,
                    onValueChange = { 
                        minGoalText = it
                        it.toDoubleOrNull()?.let { d -> vm.updateGoals(d, maxGoal) }
                    },
                    label = { Text("Minimum Spending Goal") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = maxGoalText,
                    onValueChange = { 
                        maxGoalText = it
                        it.toDoubleOrNull()?.let { d -> vm.updateGoals(minGoal, d) }
                    },
                    label = { Text("Maximum Spending Goal") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Theme Toggle
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = darkMode,
                    onCheckedChange = onToggleTheme
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logout Button
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Text("Logout")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Delete Account Button
        OutlinedButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Delete Account")
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteAccount()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

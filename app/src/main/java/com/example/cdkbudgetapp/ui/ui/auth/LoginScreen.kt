package com.example.cdkbudgetapp.ui.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "App Logo",
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text("CDK BUDGET Login", style = MaterialTheme.typography.headlineLarge)
        
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onLoginSuccess,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(Modifier.height(10.dp))

        TextButton(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create account")
        }
    }
}

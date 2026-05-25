package com.example.cdkbudgetapp.ui.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create An Account", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onRegisterSuccess,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }
    }
}

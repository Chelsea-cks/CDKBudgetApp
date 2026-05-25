package com.example.cdkbudgetapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.cdkbudgetapp.ui.ui.CDKBudgetAppTheme
import com.example.cdkbudgetapp.navigation.NavGraph
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkMode by remember { mutableStateOf(false) }
            
            CDKBudgetAppTheme(darkTheme = darkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        darkMode = darkMode,
                        onToggleTheme = { darkMode = it }
                    )
                }
            }
        }
    }
}

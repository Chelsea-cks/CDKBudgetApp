package com.example.cdkbudgetapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.cdkbudgetapp.ui.ui.auth.LoginScreen
import com.example.cdkbudgetapp.ui.ui.auth.RegisterScreen
import com.example.cdkbudgetapp.ui.ui.DashboardScreen
import com.example.cdkbudgetapp.ui.ui.SettingsScreen
import com.example.cdkbudgetapp.ui.ui.chart.AnalyticsScreen
import com.example.cdkbudgetapp.viewmodel.TransactionViewModel

@Composable
fun NavGraph(
    darkMode: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Routes.Login.route && currentRoute != Routes.Register.route) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Routes.Dashboard.route,
                        onClick = { 
                            navController.navigate(Routes.Dashboard.route) {
                                popUpTo(Routes.Dashboard.route) { inclusive = true }
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.Chart.route,
                        onClick = { navController.navigate(Routes.Chart.route) },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Graphs") },
                        label = { Text("Graphs") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.Settings.route,
                        onClick = { navController.navigate(Routes.Settings.route) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.Dashboard.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate(Routes.Register.route)
                    }
                )
            }
            composable(Routes.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Routes.Dashboard.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.Dashboard.route) {
                DashboardScreen(
                    navController = navController,
                    darkMode = darkMode,
                    onToggleTheme = onToggleTheme
                )
            }
            composable(Routes.Chart.route) {
                val vm: TransactionViewModel = viewModel()
                val transactions by vm.transactions.collectAsState()
                AnalyticsScreen(transactions, darkMode)
            }
            composable(Routes.Settings.route) {
                SettingsScreen(
                    darkMode = darkMode,
                    onToggleTheme = onToggleTheme,
                    onLogout = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onDeleteAccount = {
                        // For now, same as logout, but could involve clearing local data
                        navController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

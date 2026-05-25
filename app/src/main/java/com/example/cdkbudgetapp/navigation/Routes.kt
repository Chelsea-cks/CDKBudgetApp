package com.example.cdkbudgetapp.navigation

sealed class Routes(val route: String){
    object Login : Routes("login")
    object Register : Routes("register")
    object Dashboard : Routes("dashboard")
    object Chart : Routes("chart")
    object Settings : Routes("settings")
}
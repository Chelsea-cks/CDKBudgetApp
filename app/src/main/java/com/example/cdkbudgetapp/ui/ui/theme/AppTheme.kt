package com.example.cdkbudgetapp.ui.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun BudgetBuddyTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {

    val colorScheme = if (darkTheme) {

        darkColorScheme(
            primary = Purple80,
            secondary = PurpleGrey80,
            tertiary = Pink80
        )

    } else {

        lightColorScheme(
            primary = BluePrimary,
            secondary = BlueSecondary,
            tertiary = GreenAccent
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

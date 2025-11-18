package org.aaditx23.taskman.presentation.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.aaditx23.taskman.data.preferences.ThemePreferences

class ThemeManager(private val themePreferences: ThemePreferences) {
    val isDarkMode = themePreferences.isDarkMode

    suspend fun toggleTheme(currentValue: Boolean) {
        themePreferences.setDarkMode(!currentValue)
    }
}

@Composable
fun rememberThemeManager(context: Context): ThemeManager {
    return remember {
        ThemeManager(ThemePreferences(context))
    }
}


package org.aaditx23.taskman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import org.aaditx23.taskman.data.repository.RepositoryProvider
import org.aaditx23.taskman.presentation.theme.rememberThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize repository
        val repository = RepositoryProvider.getTaskRepository(applicationContext)

        setContent {
            val themeManager = rememberThemeManager(applicationContext)
            val isDarkMode by themeManager.isDarkMode.collectAsState(initial = false)
            val scope = rememberCoroutineScope()

            App(
                repository = repository,
                isDarkMode = isDarkMode,
                onToggleTheme = {
                    scope.launch {
                        themeManager.toggleTheme(isDarkMode)
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(null)
}
package org.aaditx23.taskman

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Navigator(screen = getTaskListScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}

// Expect function to get the platform-specific screen
expect fun getTaskListScreen(): cafe.adriel.voyager.core.screen.Screen

package org.aaditx23.taskman

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.aaditx23.taskman.data.repository.TaskRepository
import org.jetbrains.compose.ui.tooling.preview.Preview

// Store repository globally for access in screens
object AppDependencies {
    var repository: TaskRepository? = null
}

@Composable
@Preview
fun App(repository: TaskRepository? = null) {
    // Set the repository for global access
    AppDependencies.repository = repository

    MaterialTheme {
        Navigator(screen = getTaskListScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}

// Expect function to get the platform-specific screen
expect fun getTaskListScreen(): cafe.adriel.voyager.core.screen.Screen


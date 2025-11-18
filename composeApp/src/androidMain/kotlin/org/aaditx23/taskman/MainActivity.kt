package org.aaditx23.taskman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.aaditx23.taskman.data.repository.RepositoryProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize repository
        val repository = RepositoryProvider.getTaskRepository(applicationContext)

        setContent {
            App(repository)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(null)
}
package org.aaditx23.taskman.presentation.screens.taskdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.aaditx23.taskman.presentation.components.*

data class TaskDetailScreen(val taskId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = remember { TaskDetailScreenModel(taskId) }
        val state by screenModel.state.collectAsState()

        var showDeleteDialog by remember { mutableStateOf(false) }

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.task == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Task not found")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navigator.pop() }) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            val task = state.task!!

            Scaffold(
                topBar = {
                    TaskDetailTopBar(
                        isEditing = state.isEditing,
                        onBackClick = { navigator.pop() },
                        onEditClick = { screenModel.toggleEditMode() },
                        onDeleteClick = { showDeleteDialog = true }
                    )
                }
            ) { paddingValues ->
                if (state.isEditing) {
                    EditTaskContent(
                        task = task,
                        onSave = { title, description, priority, status, dueDate ->
                            screenModel.updateTask(title, description, priority, status, dueDate)
                        },
                        onCancel = { screenModel.toggleEditMode() },
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    ViewTaskContent(
                        task = task,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Task") },
                    text = { Text("Are you sure you want to delete: ${task.title}?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                screenModel.deleteTask()
                                navigator.pop()
                            }
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}


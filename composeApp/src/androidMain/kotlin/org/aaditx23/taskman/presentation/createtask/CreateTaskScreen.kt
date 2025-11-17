package org.aaditx23.taskman.presentation.createtask

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.aaditx23.taskman.domain.model.Priority
import org.aaditx23.taskman.domain.model.Status
import java.text.SimpleDateFormat
import java.util.*

class CreateTaskScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = remember { CreateTaskScreenModel() }
        val state by screenModel.state.collectAsState()

        var showDatePicker by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                CreateTaskTopBar(
                    onBackClick = { navigator.pop() }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = state.title,
                        onValueChange = { screenModel.onTitleChange(it) },
                        label = { Text("Title *") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = state.titleError,
                        supportingText = if (state.titleError) {
                            { Text("Title is required") }
                        } else null,
                        singleLine = true,
                        enabled = !state.isSaving
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.description,
                        onValueChange = { screenModel.onDescriptionChange(it) },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5,
                        enabled = !state.isSaving
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Priority",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Priority.entries.forEach { priority ->
                            FilterChip(
                                selected = state.priority == priority,
                                onClick = { screenModel.onPriorityChange(priority) },
                                label = { Text(priority.name) },
                                modifier = Modifier.weight(1f),
                                enabled = !state.isSaving
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Status.entries.forEach { status ->
                            FilterChip(
                                selected = state.status == status,
                                onClick = { screenModel.onStatusChange(status) },
                                label = {
                                    Text(when (status) {
                                        Status.TODO -> "To Do"
                                        Status.IN_PROGRESS -> "In Progress"
                                        Status.DONE -> "Done"
                                    })
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.isSaving
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Due Date",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = state.dueDate?.let {
                                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
                                    } ?: "No due date",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Row {
                                if (state.dueDate != null) {
                                    IconButton(
                                        onClick = { screenModel.onDueDateChange(null) },
                                        enabled = !state.isSaving
                                    ) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear date")
                                    }
                                }
                                IconButton(
                                    onClick = { showDatePicker = true },
                                    enabled = !state.isSaving
                                ) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = "Pick date")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { navigator.pop() },
                            modifier = Modifier.weight(1f),
                            enabled = !state.isSaving
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                screenModel.createTask {
                                    navigator.pop()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !state.isSaving && state.title.isNotBlank()
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Create Task")
                            }
                        }
                    }
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                initialDate = state.dueDate ?: System.currentTimeMillis(),
                onDateSelected = { selectedDate ->
                    screenModel.onDueDateChange(selectedDate)
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Create New Task") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}


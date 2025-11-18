package org.aaditx23.taskman.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.material.icons.filled.LibraryAddCheck
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.LibraryAddCheck
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.aaditx23.taskman.domain.model.Status
import org.aaditx23.taskman.domain.model.Task

@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onToggleInProgress: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox on the left for marking as done
            IconButton(
                onClick = onToggleComplete,
//                modifier = Modifier.align(Alignment.Top)
            ) {
                Icon(
                    if (task.status == Status.DONE) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                    contentDescription = if (task.status == Status.DONE) "Mark as incomplete" else "Mark as complete",
                    tint = if (task.status == Status.DONE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Main content
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        textDecoration = if (task.status == Status.DONE) TextDecoration.LineThrough else null
                    )
                    Row {
                        // In-progress icon
                        IconButton(onClick = onToggleInProgress) {
                            Icon(
                                imageVector =
                                    when (task.status) {
                                        Status.IN_PROGRESS -> Icons.Outlined.HourglassBottom
                                        Status.DONE -> Icons.Filled.HourglassFull
                                        else -> Icons.Filled.HourglassEmpty
                                    },
                                contentDescription = "Status ICon",
                                tint = if (task.status == Status.IN_PROGRESS) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PriorityChip(priority = task.priority)
                    StatusChip(status = task.status)

                    Spacer(modifier = Modifier.weight(1f))

                    task.dueDate?.let {
                        DueDateChip(dueDate = it)
                    }
                }
            }
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
                        onDelete()
                        showDeleteDialog = false
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


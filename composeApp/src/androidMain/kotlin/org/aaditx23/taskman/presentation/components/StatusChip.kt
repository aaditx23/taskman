package org.aaditx23.taskman.presentation.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.aaditx23.taskman.domain.model.Status

@Composable
fun StatusChip(status: Status) {
    val (text, color) = when (status) {
        Status.TODO -> "To Do" to MaterialTheme.colorScheme.secondary
        Status.IN_PROGRESS -> "In Progress" to MaterialTheme.colorScheme.tertiary
        Status.DONE -> "Done" to MaterialTheme.colorScheme.primary
    }

    AssistChip(
        onClick = { },
        label = { Text(text, style = MaterialTheme.typography.labelSmall) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.1f),
            labelColor = color
        )
    )
}


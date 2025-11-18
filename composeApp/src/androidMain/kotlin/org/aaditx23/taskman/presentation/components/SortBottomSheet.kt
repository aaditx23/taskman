package org.aaditx23.taskman.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.aaditx23.taskman.presentation.screens.tasklist.SortBy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    currentSortBy: SortBy,
    onSortSelect: (SortBy) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Sort Tasks",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val sortOptions = listOf(
                SortBy.CREATED_DATE_DESC to "Newest First",
                SortBy.CREATED_DATE_ASC to "Oldest First",
                SortBy.DUE_DATE_ASC to "Due Date (Earliest)",
                SortBy.DUE_DATE_DESC to "Due Date (Latest)",
                SortBy.PRIORITY_HIGH_TO_LOW to "Priority (High to Low)",
                SortBy.PRIORITY_LOW_TO_HIGH to "Priority (Low to High)",
                SortBy.STATUS to "Status",
                SortBy.TITLE_ASC to "Title (A-Z)",
                SortBy.TITLE_DESC to "Title (Z-A)"
            )

            sortOptions.forEach { (sortBy, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSortSelect(sortBy) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentSortBy == sortBy,
                        onClick = { onSortSelect(sortBy) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(label, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


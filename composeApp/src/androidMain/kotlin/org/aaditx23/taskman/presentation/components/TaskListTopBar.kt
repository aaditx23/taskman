package org.aaditx23.taskman.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    hasActiveFilters: Boolean,
    isDarkMode: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    var isSearchActive by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (isSearchActive) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search tasks...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                    )
                )
            } else {
                Text("Tasks")
            }
        },
        actions = {
            IconButton(onClick = {
                isSearchActive = !isSearchActive
                if (!isSearchActive) {
                    onSearchQueryChange("")
                }
            }) {
                Icon(
                    if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = if (isSearchActive) "Close search" else "Search"
                )
            }
            IconButton(onClick = onToggleTheme) {
                Icon(
                    if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = if (isDarkMode) "Switch to light mode" else "Switch to dark mode"
                )
            }
            BadgedBox(
                badge = {
                    if (hasActiveFilters) {
                        Badge()
                    }
                }
            ) {
                IconButton(onClick = onFilterClick) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filter")
                }
            }
            IconButton(onClick = onSortClick) {
                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
            }
        }
    )
}


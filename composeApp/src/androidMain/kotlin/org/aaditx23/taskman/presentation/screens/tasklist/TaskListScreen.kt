package org.aaditx23.taskman.presentation.screens.tasklist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.aaditx23.taskman.AppDependencies
import org.aaditx23.taskman.presentation.components.*
import org.aaditx23.taskman.presentation.screens.createtask.CreateTaskScreen
import org.aaditx23.taskman.presentation.screens.taskdetail.TaskDetailScreen

class TaskListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = remember { TaskListScreenModel() }
        val state by screenModel.state.collectAsState()

        var showFilterSheet by remember { mutableStateOf(false) }
        var showSortSheet by remember { mutableStateOf(false) }

        val isDarkMode = AppDependencies.isDarkMode?.value ?: false
        val onToggleTheme = AppDependencies.onToggleTheme ?: {}

        Scaffold(
            topBar = {
                TaskListTopBar(
                    searchQuery = state.searchQuery,
                    onSearchQueryChange = { screenModel.onSearchQueryChange(it) },
                    onFilterClick = { showFilterSheet = true },
                    onSortClick = { showSortSheet = true },
                    hasActiveFilters = state.selectedStatus != null || state.selectedPriority != null,
                    isDarkMode = isDarkMode,
                    onToggleTheme = onToggleTheme
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigator.push(CreateTaskScreen()) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (state.filteredTasks.isEmpty()) {
                    EmptyState(
                        hasFilters = state.searchQuery.isNotBlank() ||
                                   state.selectedStatus != null ||
                                   state.selectedPriority != null,
                        onClearFilters = { screenModel.clearFilters() }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = state.filteredTasks,
                            key = { it.id }
                        ) { task ->
                            TaskItem(
                                task = task,
                                onClick = { navigator.push(TaskDetailScreen(task.id)) },
                                onToggleComplete = { screenModel.toggleTaskCompletion(task.id) },
                                onDelete = { screenModel.deleteTask(task.id) }
                            )
                        }
                    }
                }
            }
        }

        if (showFilterSheet) {
            FilterBottomSheet(
                selectedStatus = state.selectedStatus,
                selectedPriority = state.selectedPriority,
                onStatusSelect = { screenModel.onStatusFilterChange(it) },
                onPrioritySelect = { screenModel.onPriorityFilterChange(it) },
                onDismiss = { showFilterSheet = false },
                onClearFilters = {
                    screenModel.clearFilters()
                    showFilterSheet = false
                }
            )
        }

        if (showSortSheet) {
            SortBottomSheet(
                currentSortBy = state.sortBy,
                onSortSelect = {
                    screenModel.onSortByChange(it)
                    showSortSheet = false
                },
                onDismiss = { showSortSheet = false }
            )
        }
    }
}


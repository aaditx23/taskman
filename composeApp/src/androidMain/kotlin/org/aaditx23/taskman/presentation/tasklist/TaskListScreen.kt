package org.aaditx23.taskman.presentation.tasklist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.aaditx23.taskman.domain.model.Priority
import org.aaditx23.taskman.domain.model.Status
import org.aaditx23.taskman.domain.model.Task
import org.aaditx23.taskman.presentation.taskdetail.TaskDetailScreen
import org.aaditx23.taskman.presentation.createtask.CreateTaskScreen
import java.text.SimpleDateFormat
import java.util.*

class TaskListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = remember { TaskListScreenModel() }
        val state by screenModel.state.collectAsState()

        var showFilterSheet by remember { mutableStateOf(false) }
        var showSortSheet by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TaskListTopBar(
                    searchQuery = state.searchQuery,
                    onSearchQueryChange = { screenModel.onSearchQueryChange(it) },
                    onFilterClick = { showFilterSheet = true },
                    onSortClick = { showSortSheet = true },
                    hasActiveFilters = state.selectedStatus != null || state.selectedPriority != null
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    hasActiveFilters: Boolean
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

@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task?") },
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

@Composable
fun PriorityChip(priority: Priority) {
    val color = when (priority) {
        Priority.HIGH -> MaterialTheme.colorScheme.error
        Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
        Priority.LOW -> MaterialTheme.colorScheme.primary
    }

    AssistChip(
        onClick = { },
        label = { Text(priority.name, style = MaterialTheme.typography.labelSmall) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.1f),
            labelColor = color
        )
    )
}

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

@Composable
fun DueDateChip(dueDate: Long) {
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    val isOverdue = dueDate < System.currentTimeMillis()

    AssistChip(
        onClick = { },
        label = {
            Text(
                dateFormat.format(Date(dueDate)),
                style = MaterialTheme.typography.labelSmall
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.CalendarToday,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isOverdue)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.surfaceVariant,
            labelColor = if (isOverdue)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
fun EmptyState(
    hasFilters: Boolean,
    onClearFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.TaskAlt,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (hasFilters) "No tasks found" else "No tasks yet",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (hasFilters) "Try adjusting your filters" else "Create your first task to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (hasFilters) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onClearFilters) {
                Text("Clear Filters")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    selectedStatus: Status?,
    selectedPriority: Priority?,
    onStatusSelect: (Status?) -> Unit,
    onPrioritySelect: (Priority?) -> Unit,
    onDismiss: () -> Unit,
    onClearFilters: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Filter Tasks",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                "Status",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStatus == null,
                    onClick = { onStatusSelect(null) },
                    label = { Text("All") }
                )
                Status.entries.forEach { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { onStatusSelect(status) },
                        label = {
                            Text(when (status) {
                                Status.TODO -> "To Do"
                                Status.IN_PROGRESS -> "In Progress"
                                Status.DONE -> "Done"
                            })
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Priority",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedPriority == null,
                    onClick = { onPrioritySelect(null) },
                    label = { Text("All") }
                )
                Priority.entries.forEach { priority ->
                    FilterChip(
                        selected = selectedPriority == priority,
                        onClick = { onPrioritySelect(priority) },
                        label = { Text(priority.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClearFilters,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear All")
                }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

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


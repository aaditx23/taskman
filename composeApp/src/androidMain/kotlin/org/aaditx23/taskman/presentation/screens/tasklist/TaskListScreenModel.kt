package org.aaditx23.taskman.presentation.screens.tasklist

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.aaditx23.taskman.AppDependencies
import org.aaditx23.taskman.domain.model.Priority
import org.aaditx23.taskman.domain.model.Status
import org.aaditx23.taskman.domain.model.Task

data class TaskListState(
    val tasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val searchQuery: String = "",
    val selectedStatus: Status? = null,
    val selectedPriority: Priority? = null,
    val sortBy: SortBy = SortBy.CREATED_DATE_DESC,
    val isLoading: Boolean = false
)

enum class SortBy {
    CREATED_DATE_ASC,
    CREATED_DATE_DESC,
    DUE_DATE_ASC,
    DUE_DATE_DESC,
    PRIORITY_HIGH_TO_LOW,
    PRIORITY_LOW_TO_HIGH,
    STATUS,
    TITLE_ASC,
    TITLE_DESC
}

class TaskListScreenModel : ScreenModel {
    private val _state = MutableStateFlow(TaskListState())
    val state: StateFlow<TaskListState> = _state.asStateFlow()

    private val repository = AppDependencies.repository

    init {
        loadTasks()
    }

    fun loadTasks() {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            repository?.let { repo ->
                repo.getAllTasks()
                    .collect { tasks ->
                        _state.value = _state.value.copy(
                            tasks = tasks,
                            filteredTasks = applyFiltersAndSort(tasks),
                            isLoading = false
                        )
                    }
            } ?: run {
                // Fallback if no repository (preview mode)
                _state.value = _state.value.copy(
                    tasks = emptyList(),
                    filteredTasks = emptyList(),
                    isLoading = false
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        applyFilters()
    }

    fun onStatusFilterChange(status: Status?) {
        _state.value = _state.value.copy(selectedStatus = status)
        applyFilters()
    }

    fun onPriorityFilterChange(priority: Priority?) {
        _state.value = _state.value.copy(selectedPriority = priority)
        applyFilters()
    }

    fun onSortByChange(sortBy: SortBy) {
        _state.value = _state.value.copy(sortBy = sortBy)
        applyFilters()
    }

    fun clearFilters() {
        _state.value = _state.value.copy(
            searchQuery = "",
            selectedStatus = null,
            selectedPriority = null,
            sortBy = SortBy.CREATED_DATE_DESC
        )
        applyFilters()
    }

    private fun applyFilters() {
        val filtered = applyFiltersAndSort(_state.value.tasks)
        _state.value = _state.value.copy(filteredTasks = filtered)
    }

    private fun applyFiltersAndSort(tasks: List<Task>): List<Task> {
        var result = tasks

        // Apply search filter
        if (_state.value.searchQuery.isNotBlank()) {
            result = result.filter {
                it.title.contains(_state.value.searchQuery, ignoreCase = true) ||
                        it.description.contains(_state.value.searchQuery, ignoreCase = true)
            }
        }

        // Apply status filter
        _state.value.selectedStatus?.let { status ->
            result = result.filter { it.status == status }
        }

        // Apply priority filter
        _state.value.selectedPriority?.let { priority ->
            result = result.filter { it.priority == priority }
        }

        // Apply sorting
        result = when (_state.value.sortBy) {
            SortBy.CREATED_DATE_ASC -> result.sortedBy { it.createdAt }
            SortBy.CREATED_DATE_DESC -> result.sortedByDescending { it.createdAt }
            SortBy.DUE_DATE_ASC -> result.sortedBy { it.dueDate ?: Long.MAX_VALUE }
            SortBy.DUE_DATE_DESC -> result.sortedByDescending { it.dueDate ?: Long.MIN_VALUE }
            SortBy.PRIORITY_HIGH_TO_LOW -> result.sortedByDescending { it.priority.ordinal }
            SortBy.PRIORITY_LOW_TO_HIGH -> result.sortedBy { it.priority.ordinal }
            SortBy.STATUS -> result.sortedBy { it.status.ordinal }
            SortBy.TITLE_ASC -> result.sortedBy { it.title }
            SortBy.TITLE_DESC -> result.sortedByDescending { it.title }
        }

        return result
    }

    fun deleteTask(taskId: String) {
        screenModelScope.launch {
            repository?.deleteTask(taskId)
            // Tasks will automatically update via Flow
        }
    }

    fun toggleTaskCompletion(taskId: String) {
        screenModelScope.launch {
            repository?.getTaskById(taskId)?.let { task ->
                val updatedTask = task.copy(
                    status = if (task.status == Status.DONE) Status.TODO else Status.DONE
                )
                repository?.updateTask(updatedTask)
                // Tasks will automatically update via Flow
            }
        }
    }

    fun toggleTaskInProgress(taskId: String) {
        screenModelScope.launch {
            repository?.getTaskById(taskId)?.let { task ->
                val updatedTask = task.copy(
                    status = if (task.status == Status.IN_PROGRESS) Status.TODO else Status.IN_PROGRESS
                )
                repository?.updateTask(updatedTask)
                // Tasks will automatically update via Flow
            }
        }
    }
}


package org.aaditx23.taskman.presentation.screens.taskdetail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.aaditx23.taskman.AppDependencies
import org.aaditx23.taskman.domain.model.Priority
import org.aaditx23.taskman.domain.model.Status
import org.aaditx23.taskman.domain.model.Task

data class TaskDetailState(
    val task: Task? = null,
    val isLoading: Boolean = true,
    val isEditing: Boolean = false
)

class TaskDetailScreenModel(private val taskId: String) : ScreenModel {
    private val _state = MutableStateFlow(TaskDetailState())
    val state: StateFlow<TaskDetailState> = _state.asStateFlow()

    private val repository = AppDependencies.repository

    init {
        loadTask()
    }

    private fun loadTask() {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val task = repository?.getTaskById(taskId)
            _state.value = _state.value.copy(
                task = task,
                isLoading = false
            )
        }
    }

    fun toggleEditMode() {
        _state.value = _state.value.copy(isEditing = !_state.value.isEditing)
    }

    fun updateTask(
        title: String,
        description: String,
        priority: Priority,
        status: Status,
        dueDate: Long?
    ) {
        screenModelScope.launch {
            val currentTask = _state.value.task ?: return@launch
            val updatedTask = currentTask.copy(
                title = title,
                description = description,
                priority = priority,
                status = status,
                dueDate = dueDate
            )

            repository?.updateTask(updatedTask)

            _state.value = _state.value.copy(
                task = updatedTask,
                isEditing = false
            )
        }
    }

    fun deleteTask() {
        screenModelScope.launch {
            repository?.deleteTask(taskId)
        }
    }
}


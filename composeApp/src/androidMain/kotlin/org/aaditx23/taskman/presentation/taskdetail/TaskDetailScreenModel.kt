package org.aaditx23.taskman.presentation.taskdetail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.aaditx23.taskman.data.MockData
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

    init {
        loadTask()
    }

    private fun loadTask() {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            delay(200)
            val task = MockData.tasks.find { it.id == taskId }
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

            val index = MockData.tasks.indexOfFirst { it.id == taskId }
            if (index != -1) {
                MockData.tasks[index] = updatedTask
            }

            _state.value = _state.value.copy(
                task = updatedTask,
                isEditing = false
            )
        }
    }

    fun deleteTask() {
        MockData.tasks.removeAll { it.id == taskId }
    }
}


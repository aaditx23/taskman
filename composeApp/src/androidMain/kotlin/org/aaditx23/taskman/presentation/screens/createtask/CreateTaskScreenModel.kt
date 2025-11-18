package org.aaditx23.taskman.presentation.screens.createtask

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
import java.util.UUID

data class CreateTaskState(
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val status: Status = Status.TODO,
    val dueDate: Long? = null,
    val titleError: Boolean = false,
    val isSaving: Boolean = false
)

class CreateTaskScreenModel : ScreenModel {
    private val _state = MutableStateFlow(CreateTaskState())
    val state: StateFlow<CreateTaskState> = _state.asStateFlow()

    private val repository = AppDependencies.repository

    fun onTitleChange(title: String) {
        _state.value = _state.value.copy(
            title = title,
            titleError = false
        )
    }

    fun onDescriptionChange(description: String) {
        _state.value = _state.value.copy(description = description)
    }

    fun onPriorityChange(priority: Priority) {
        _state.value = _state.value.copy(priority = priority)
    }

    fun onStatusChange(status: Status) {
        _state.value = _state.value.copy(status = status)
    }

    fun onDueDateChange(dueDate: Long?) {
        _state.value = _state.value.copy(dueDate = dueDate)
    }

    fun createTask(onSuccess: () -> Unit) {
        if (_state.value.title.isBlank()) {
            _state.value = _state.value.copy(titleError = true)
            return
        }

        screenModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)

            val newTask = Task(
                id = UUID.randomUUID().toString(),
                title = _state.value.title,
                description = _state.value.description,
                priority = _state.value.priority,
                status = _state.value.status,
                dueDate = _state.value.dueDate,
                createdAt = System.currentTimeMillis()
            )

            repository?.insertTask(newTask)

            _state.value = _state.value.copy(isSaving = false)
            onSuccess()
        }
    }
}


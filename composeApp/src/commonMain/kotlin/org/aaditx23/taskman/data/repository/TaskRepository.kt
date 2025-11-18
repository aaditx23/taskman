package org.aaditx23.taskman.data.repository

import kotlinx.coroutines.flow.Flow
import org.aaditx23.taskman.domain.model.Task

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    suspend fun getTaskById(id: String): Task?
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: String)
    suspend fun deleteAllTasks()
}


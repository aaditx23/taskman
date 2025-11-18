package org.aaditx23.taskman.data.repository

import android.content.Context
import org.aaditx23.taskman.data.database.TaskDatabase

object RepositoryProvider {
    private var taskRepository: TaskRepository? = null

    fun getTaskRepository(context: Context): TaskRepository {
        return taskRepository ?: synchronized(this) {
            val database = TaskDatabase.getDatabase(context)
            val repo = TaskRepositoryImpl(database.taskDao())
            taskRepository = repo
            repo
        }
    }
}


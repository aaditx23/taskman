package org.aaditx23.taskman.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val priority: String, // Store as string: HIGH, MEDIUM, LOW
    val status: String,   // Store as string: TODO, IN_PROGRESS, DONE
    val dueDate: Long?,   // Nullable timestamp
    val createdAt: Long
)


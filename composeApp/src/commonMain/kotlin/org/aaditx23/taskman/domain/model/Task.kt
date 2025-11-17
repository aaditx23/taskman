package org.aaditx23.taskman.domain.model

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class Task @OptIn(ExperimentalTime::class) constructor(
    val id: String,
    val title: String,
    val description: String = "",
    val priority: Priority,
    val status: Status,
    val dueDate: Long? = null,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)

enum class Priority {
    LOW, MEDIUM, HIGH
}

enum class Status {
    TODO, IN_PROGRESS, DONE
}


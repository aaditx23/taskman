package org.aaditx23.taskman.data

import org.aaditx23.taskman.domain.model.Priority
import org.aaditx23.taskman.domain.model.Status
import org.aaditx23.taskman.domain.model.Task
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object MockData {
    @OptIn(ExperimentalTime::class)
    val tasks = mutableListOf(
        Task(
            id = "1",
            title = "Complete project documentation",
            description = "Write comprehensive documentation for the task management app",
            priority = Priority.HIGH,
            status = Status.IN_PROGRESS,
            dueDate = Clock.System.now().toEpochMilliseconds() + 86400000, // Tomorrow
            createdAt = Clock.System.now().toEpochMilliseconds() - 172800000 // 2 days ago
        ),
        Task(
            id = "2",
            title = "Review pull requests",
            description = "Review and merge pending pull requests from team members",
            priority = Priority.MEDIUM,
            status = Status.TODO,
            dueDate = Clock.System.now().toEpochMilliseconds() + 259200000, // 3 days from now
            createdAt = Clock.System.now().toEpochMilliseconds() - 86400000
        ),
        Task(
            id = "3",
            title = "Fix login bug",
            description = "Users reporting issues with login on Android devices",
            priority = Priority.HIGH,
            status = Status.TODO,
            dueDate = Clock.System.now().toEpochMilliseconds() + 43200000, // 12 hours from now
            createdAt = Clock.System.now().toEpochMilliseconds() - 3600000
        ),
        Task(
            id = "4",
            title = "Update dependencies",
            description = "Update all project dependencies to latest stable versions",
            priority = Priority.LOW,
            status = Status.DONE,
            dueDate = null,
            createdAt = Clock.System.now().toEpochMilliseconds() - 604800000 // 7 days ago
        ),
        Task(
            id = "5",
            title = "Implement dark mode",
            description = "Add dark mode support across the application",
            priority = Priority.MEDIUM,
            status = Status.IN_PROGRESS,
            dueDate = Clock.System.now().toEpochMilliseconds() + 432000000, // 5 days from now
            createdAt = Clock.System.now().toEpochMilliseconds() - 259200000
        ),
        Task(
            id = "6",
            title = "Setup CI/CD pipeline",
            description = "Configure automated testing and deployment pipeline",
            priority = Priority.HIGH,
            status = Status.TODO,
            dueDate = Clock.System.now().toEpochMilliseconds() + 172800000, // 2 days from now
            createdAt = Clock.System.now().toEpochMilliseconds() - 43200000
        ),
        Task(
            id = "7",
            title = "Design new app icon",
            description = "",
            priority = Priority.LOW,
            status = Status.TODO,
            dueDate = null,
            createdAt = Clock.System.now().toEpochMilliseconds() - 518400000
        ),
        Task(
            id = "8",
            title = "Optimize database queries",
            description = "Improve performance of database operations",
            priority = Priority.MEDIUM,
            status = Status.DONE,
            dueDate = null,
            createdAt = Clock.System.now().toEpochMilliseconds() - 864000000
        )
    )
}


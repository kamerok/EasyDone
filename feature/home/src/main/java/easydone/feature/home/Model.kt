package easydone.feature.home

import java.time.LocalDate


internal data class State(
    val inboxCount: Int,
    val todoTasks: List<UiTask>,
    val nextWaitingTask: Pair<UiTask, LocalDate>?,
    val waitingCount: Int,
    val maybeTasks: List<UiTask>
)

internal data class UiTask(
    val id: String,
    val title: String,
    val hasDescription: Boolean,
    val isUrgent: Boolean,
    val isImportant: Boolean
)

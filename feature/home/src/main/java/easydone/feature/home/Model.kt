package easydone.feature.home

import easydone.coreui.design.UiTask
import java.time.LocalDate


internal data class State(
    val isSyncing: Boolean,
    val hasChanges: Boolean,
    val inboxCount: Int,
    val todoTasks: List<UiTask>,
    val nextWaitingTask: Pair<UiTask, LocalDate>?,
    val waitingCount: Int,
    val maybeTasks: List<UiTask>
)

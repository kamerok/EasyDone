package easydone.feature.home

import easydone.coreui.design.UiTask
import java.time.LocalDate


internal data class State(
    val isSyncing: Boolean,
    val hasChanges: Boolean,
    val inboxCount: Int,
    val todoTasks: List<UiTask>,
    val nextWaitingTasks: NextWaitingTasks?,
    val waitingCount: Int,
    val projectTasks: List<UiTask>,
    val maybeTasks: List<UiTask>
)

internal data class NextWaitingTasks(
    val date: LocalDate,
    val tasks: List<UiTask>
)

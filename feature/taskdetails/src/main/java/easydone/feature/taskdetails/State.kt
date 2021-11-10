package easydone.feature.taskdetails

import easydone.core.domain.model.Task
import java.time.LocalDate


internal data class State(
    val type: Task.Type,
    val date: LocalDate?,
    val typeText: String,
    val title: String,
    val description: String,
    val isUrgent: Boolean,
    val isImportant: Boolean
)

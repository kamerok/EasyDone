package easydone.feature.feed

import easydone.core.model.Task
import kotlin.random.Random


internal data class TaskUiModel(
    val id: String,
    val title: String,
    val hasDescription: Boolean,
    val isUrgent: Boolean,
    val isImportant: Boolean
)

internal fun Task.toUi() = TaskUiModel(
    id = id,
    title = title,
    hasDescription = description.isNotEmpty(),
    isImportant = Random.nextBoolean(),
    isUrgent = Random.nextBoolean()
)

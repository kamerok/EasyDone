package easydone.core.domain.model


class TaskTemplate private constructor(
    val type: Task.Type,
    val title: String,
    val description: String,
    val isUrgent: Boolean,
    val isImportant: Boolean
) {

    companion object {
        fun create(
            type: Task.Type,
            title: String,
            description: String,
            isUrgent: Boolean,
            isImportant: Boolean
        ): Result<TaskTemplate> =
            if (title.isBlank()) {
                Result.failure(EmptyTitleError)
            } else {
                Result.success(
                    TaskTemplate(
                        type,
                        title,
                        description,
                        isUrgent,
                        isImportant
                    )
                )
            }
    }
}

object EmptyTitleError : IllegalArgumentException("Task title should not be empty")

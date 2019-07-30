package easydone.core.model

import java.util.Date


data class Task constructor(
    val id: String,
    val type: Type,
    val title: String,
    val description: String,
    val dueDate: Date?,
    val isDone: Boolean
) {

    enum class Type {
        INBOX, TO_DO
    }

}
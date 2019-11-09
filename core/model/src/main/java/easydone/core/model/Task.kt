package easydone.core.model

import org.threeten.bp.LocalDate


data class Task constructor(
    val id: String,
    val type: Type,
    val title: String,
    val description: String,
    val dueDate: LocalDate?,
    val isDone: Boolean
) {

    enum class Type {
        INBOX, TO_DO, WAITING
    }

}

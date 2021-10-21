package easydone.core.domain.model

import java.time.LocalDate


data class Task(
    val id: String,
    val type: Type,
    val title: String,
    val description: String,
    val dueDate: LocalDate?,
    val markers: Markers,
    val isDone: Boolean
) {

    enum class Type {
        INBOX, TO_DO, WAITING, MAYBE
    }

}

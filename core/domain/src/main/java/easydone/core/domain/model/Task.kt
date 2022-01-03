package easydone.core.domain.model

import java.time.LocalDate


data class Task(
    val id: String,
    val type: Type,
    val title: String,
    val description: String,
    val markers: Markers,
    val isDone: Boolean
) {

    sealed class Type {
        object Inbox : Type()
        object ToDo : Type()
        data class Waiting(val date: LocalDate) : Type()
        object Project : Type()
        object Maybe : Type()
    }

}

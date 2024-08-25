package easydone.core.domain.model

import java.io.Serializable
import java.time.LocalDate


data class Task(
    val id: String,
    val type: Type,
    val title: String,
    val description: String,
    val markers: Markers,
    val isDone: Boolean
) {

    sealed class Type: Serializable {
        data object Inbox : Type()
        data object ToDo : Type()
        data class Waiting(val date: LocalDate) : Type()
        data object Project : Type()
        data object Maybe : Type()
    }

}

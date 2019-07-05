package easydone.core.model


data class Task(
    val id: String,
    val type: Type,
    val title: String,
    val description: String,
    val isDone: Boolean
) {

    enum class Type {
        INBOX, TO_DO
    }

}
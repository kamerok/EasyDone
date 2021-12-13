package easydone.service.trello.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Label(
    val id: String,
    val name: String
)

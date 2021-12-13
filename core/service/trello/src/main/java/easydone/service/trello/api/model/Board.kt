package easydone.service.trello.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Board(
    val id: String,
    val name: String
)

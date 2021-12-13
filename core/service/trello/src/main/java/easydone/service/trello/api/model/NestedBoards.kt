package easydone.service.trello.api.model

import kotlinx.serialization.Serializable

@Serializable
data class NestedBoards(val boards: List<Board>)

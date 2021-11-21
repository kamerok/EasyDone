package easydone.library.trelloapi.model

import kotlinx.serialization.Serializable

@Serializable
data class NestedBoards(val boards: List<Board>)

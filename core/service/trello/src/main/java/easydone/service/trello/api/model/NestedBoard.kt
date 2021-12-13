package easydone.service.trello.api.model

import kotlinx.serialization.Serializable

@Serializable
data class NestedBoard(
    val cards: List<Card>,
    val lists: List<CardList>,
    val labels: List<Label>
)

package easydone.library.trelloapi.model


data class NestedBoard(
    val cards: List<Card>,
    val lists: List<CardList>,
    val labels: List<Label>
)

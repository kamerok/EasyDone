package easydone.library.trelloapi.model

import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val id: String,
    val idList: String,
    val name: String,
    val desc: String,
    val due: String?,
    val pos: Float,
    val idLabels: List<String>
)

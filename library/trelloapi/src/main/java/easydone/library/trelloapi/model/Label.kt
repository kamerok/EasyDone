package easydone.library.trelloapi.model

import kotlinx.serialization.Serializable

@Serializable
data class Label(
    val id: String,
    val name: String
)

package com.kamer.trelloapi.model


data class Card(
    val id: String,
    val idList: String,
    val name: String,
    val pos: Float
)
package com.kamer.easydone


object LoginHolder {

    var isLogged = false

    var isBoardSelected = false

    val isSet
        get() = isLogged && isBoardSelected

}
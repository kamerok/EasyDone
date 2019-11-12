package com.kamer.builder


interface DeepLinkNavigator {

    fun execute(command: NavigationCommand)

}

sealed class NavigationCommand {

    data class EditTask(val id: String): NavigationCommand()

}

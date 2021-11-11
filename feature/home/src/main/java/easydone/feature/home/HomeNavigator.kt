package easydone.feature.home


interface HomeNavigator {

    fun navigateToCreate()

    fun navigateToSettings()

    fun navigateToInbox()

    fun navigateToWaiting()

    fun navigateToTask(id: String)

}

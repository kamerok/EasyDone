package easydone.feature.setupflow


interface SetupFlowNavigator {

    fun navigateToLogin(loginListener: (String, String) -> Unit)

    fun navigateToSelectBoard(token: String, userId: String, listener: (String) -> Unit)

}
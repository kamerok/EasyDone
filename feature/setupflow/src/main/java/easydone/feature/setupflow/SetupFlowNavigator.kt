package easydone.feature.setupflow

import easydone.library.trelloapi.model.Board


interface SetupFlowNavigator {

    fun navigateToLogin(loginListener: (String, List<Board>) -> Unit)

    fun navigateToSelectBoard(boards: List<Board>, listener: (String) -> Unit)

}
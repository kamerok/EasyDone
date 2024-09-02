package easydone.feature.setupflow

import easydone.feature.selectboard.BoardUiModel
import easydone.service.trello.api.model.Board


internal sealed class UiState {
    data class Login(
        val onSuccessLogin: (String, List<Board>) -> Unit
    ) : UiState()

    data class SelectBoard(
        val boards: List<BoardUiModel>,
        val onBoardSelected: (String) -> Unit,
        val onBack: () -> Unit
    ) : UiState()
}

internal sealed class Event {
    data object FinishSetup : Event()
}
package easydone.feature.login

internal sealed class UiState {

    internal data class IdleState(
        val onLoginWithTrello: () -> Unit
    ) : UiState()

    internal data object LoadingState : UiState()

    internal data class ErrorState(
        val message: String,
        val onRetry: () -> Unit
    ) : UiState()

}

internal sealed class Event {

    internal data object StartTrelloLogin : Event()

}
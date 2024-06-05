package easydone.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easydone.service.trello.api.TrelloApi
import easydone.service.trello.api.model.Board
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class LoginViewModel(
    private val api: TrelloApi,
    private val apiKey: String,
    private val successLogin: (String, List<Board>) -> Unit
) : ViewModel() {

    private val eventChannel = Channel<Event>(Channel.UNLIMITED)

    private val onStartLogin: () -> Unit = {
        mutableStateFlow.update { UiState.LoadingState }
        eventChannel.trySend(Event.StartTrelloLogin)
    }

    private val mutableStateFlow: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.IdleState(onStartLogin))

    val state: StateFlow<UiState> = mutableStateFlow.asStateFlow()

    val events: Flow<Event> get() = eventChannel.receiveAsFlow()

    fun onTokenReceived(token: String) {
        viewModelScope.launch {
            try {
                val nestedBoards = api.boards(apiKey, token)
                successLogin(token, nestedBoards.boards)
                mutableStateFlow.update { UiState.IdleState(onStartLogin) }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    UiState.ErrorState(
                        message = e.toString(),
                        onRetry = onStartLogin
                    )
                }
            }
        }
    }

}
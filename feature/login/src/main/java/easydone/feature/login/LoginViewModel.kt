package easydone.feature.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

internal class LoginViewModel : ViewModel() {

    private val eventChannel = Channel<Event>(Channel.UNLIMITED)

    private val onStartLogin: () -> Unit = { eventChannel.trySend(Event.StartTrelloLogin) }

    val state: StateFlow<UiState> = MutableStateFlow(UiState.IdleState(onStartLogin))

    val events: Flow<Event> get() = eventChannel.receiveAsFlow()

}
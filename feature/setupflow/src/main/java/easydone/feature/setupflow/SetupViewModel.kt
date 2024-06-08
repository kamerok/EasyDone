package easydone.feature.setupflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easydone.feature.selectboard.BoardUiModel
import easydone.service.trello.TrelloRemoteDataSource
import easydone.service.trello.api.model.Board
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class SetupViewModel(
    private val trelloRemoteDataSource: TrelloRemoteDataSource,
    private val onFinishSetup: () -> Unit
) : ViewModel() {

    private val onSuccessLogin: (String, List<Board>) -> Unit = { token, boards ->
        mutableState.update {
            UiState.SelectBoard(
                boards.map { BoardUiModel(it.id, it.name) },
                onBoardSelected = { boardId ->
                    saveData(token, boardId)
                },
                onBack = { mutableState.update { loginState } })
        }
    }

    private val loginState = UiState.Login(onSuccessLogin)

    private val mutableState = MutableStateFlow<UiState>(loginState)

    val state: StateFlow<UiState> = mutableState.asStateFlow()

    private fun saveData(token: String, boardId: String) {
        viewModelScope.launch {
            trelloRemoteDataSource.connect(token, boardId)
            onFinishSetup()
        }
    }
}
package easydone.feature.setupflow

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import easydone.feature.login.LoginRoute
import easydone.feature.selectboard.SelectBoardScreen
import easydone.service.trello.TrelloRemoteDataSource
import easydone.service.trello.api.TrelloApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@Composable
fun SetupRoute(
    trelloRemoteDataSource: TrelloRemoteDataSource,
    trelloApi: TrelloApi,
    trelloApiKey: String,
    onFinishSetup: () -> Unit
) {
    val viewModel: SetupViewModel = viewModel {
        SetupViewModel(trelloRemoteDataSource)
    }

    LaunchedEffect(viewModel) {
        viewModel.events
            .onEach {
                when (it) {
                    is Event.FinishSetup -> onFinishSetup()
                }
            }
            .launchIn(this)
    }

    val state: UiState by viewModel.state.collectAsStateWithLifecycle()

    SetupScreen(
        trelloApi = trelloApi,
        trelloApiKey = trelloApiKey,
        state = state
    )
}

@Composable
private fun SetupScreen(
    trelloApi: TrelloApi,
    trelloApiKey: String,
    state: UiState
) {

    BackHandler(state is UiState.SelectBoard) {
        (state as UiState.SelectBoard).onBack()
    }

    when (state) {
        is UiState.Login -> LoginRoute(
            apiKey = trelloApiKey,
            api = trelloApi,
            onSuccessLogin = state.onSuccessLogin
        )

        is UiState.SelectBoard -> SelectBoardScreen(
            boards = state.boards,
            onBoardSelected = state.onBoardSelected
        )
    }
}
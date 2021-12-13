package easydone.feature.setupflow

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kamer.setupflow.R
import easydone.feature.login.LoginFragment
import easydone.feature.login.TokenProvider
import easydone.feature.selectboard.BoardUiModel
import easydone.feature.selectboard.SelectBoardFragment
import easydone.library.navigation.FragmentManagerNavigator
import easydone.library.navigation.Navigator
import easydone.service.trello.TrelloRemoteDataSource
import easydone.service.trello.api.TrelloApi
import easydone.service.trello.api.model.Board
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class SetupFragment(
    private val trelloRemoteDataSource: TrelloRemoteDataSource,
    private val trelloApi: TrelloApi,
    private val trelloApiKey: String,
    private val tokenFlow: Flow<String>,
    private val onFinishSetup: () -> Unit
) : Fragment(R.layout.fragment_setup) {

    private val localNavigator: Navigator by lazy {
        FragmentManagerNavigator(childFragmentManager, R.id.container)
    }

    private var isLogin = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startLogin()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!isLogin) {
                        startLogin()
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                        isEnabled = true
                    }
                }
            })
    }

    private fun startLogin() {
        isLogin = true
        localNavigator.openScreen(
            LoginFragment.create(
                LoginFragment.Dependencies(
                    loginListener = { token, boards ->
                        startSelectBoard(token, boards)
                    },
                    api = trelloApi,
                    apiKey = trelloApiKey,
                    tokenProvider = object : TokenProvider {
                        override fun observeToken(): Flow<String> = tokenFlow
                    }
                )
            )
        )
    }

    private fun startSelectBoard(token: String, boards: List<Board>) {
        isLogin = false
        localNavigator.openScreen(
            SelectBoardFragment.create(
                SelectBoardFragment.Dependencies(
                    boards = boards.map { BoardUiModel(it.id, it.name) },
                    listener = { boardId ->
                        lifecycleScope.launch {
                            saveData(token, boardId)
                            onFinishSetup()
                        }
                    }
                )
            )
        )
    }

    private suspend fun saveData(token: String, boardId: String) =
        trelloRemoteDataSource.connect(token, boardId)

}

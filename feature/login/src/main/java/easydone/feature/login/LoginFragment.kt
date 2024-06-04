package easydone.feature.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import easydone.service.trello.api.TrelloApi
import easydone.service.trello.api.model.Board
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private lateinit var listener: (String, List<Board>) -> Unit
    private lateinit var api: TrelloApi
    private lateinit var apiKey: String

    private val viewModel: LoginViewModel by viewModels(factoryProducer = {
        viewModelFactory {
            initializer {
                LoginViewModel()
            }
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            LoginScreen(
                viewModel,
                apiKey,
                onTokenReceived = ::processToken
            )
        }
    }

    private fun processToken(token: String) {
        lifecycleScope.launch {
            try {
                val nestedBoards = api.boards(apiKey, token)
                successLogin(token, nestedBoards.boards)
            } catch (e: Exception) {
                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun successLogin(token: String, boards: List<Board>) {
        listener(token, boards)
    }

    data class Dependencies(
        val loginListener: (String, List<Board>) -> Unit,
        val api: TrelloApi,
        val apiKey: String,
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = LoginFragment().apply {
            listener = dependencies.loginListener
            api = dependencies.api
            apiKey = dependencies.apiKey
        }
    }
}

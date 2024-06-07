package easydone.feature.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import easydone.service.trello.api.TrelloApi
import easydone.service.trello.api.model.Board


class LoginFragment : Fragment() {

    private lateinit var listener: (String, List<Board>) -> Unit
    private lateinit var api: TrelloApi
    private lateinit var apiKey: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            LoginRoute(apiKey = apiKey, api = api, onSuccessLogin = listener)
        }
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

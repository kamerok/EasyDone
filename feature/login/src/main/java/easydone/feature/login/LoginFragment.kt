package easydone.feature.login


import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kamer.login.R
import easydone.library.trelloapi.TrelloApi
import easydone.library.trelloapi.model.Board
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var listener: (String, List<Board>) -> Unit
    private lateinit var api: TrelloApi
    private lateinit var tokenProvider: TokenProvider

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loginButton.setOnClickListener {
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()

            val uri = Uri.parse(URL)
            try {
                customTabsIntent.launchUrl(requireContext(), uri)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        }
        tokenProvider.observeToken()
            .onEach {
                processToken(it)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        view.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(
                top = insets.systemWindowInsetTop,
                bottom = insets.systemWindowInsetBottom
            )
            insets.consumeSystemWindowInsets()
        }
    }

    private fun processToken(token: String) {
        lifecycleScope.launch {
            try {
                val nestedBoards = api.boards(TrelloApi.API_KEY, token)
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
        val tokenProvider: TokenProvider
    )

    companion object {
        private const val URL =
            "https://trello.com/1/authorize?expiration=never&name=EasyDone&scope=read,write&response_type=token&key=${TrelloApi.API_KEY}&callback_method=fragment&return_url=easydone://auth"

        fun create(dependencies: Dependencies): Fragment = LoginFragment().apply {
            listener = dependencies.loginListener
            api = dependencies.api
            tokenProvider = dependencies.tokenProvider
        }
    }
}

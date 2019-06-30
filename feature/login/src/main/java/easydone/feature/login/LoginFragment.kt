package easydone.feature.login


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.kamer.login.R
import easydone.library.trelloapi.TrelloApi
import easydone.library.trelloapi.model.Board
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginFragment : Fragment() {

    private lateinit var listener: (String, List<Board>) -> Unit
    private lateinit var api: TrelloApi

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        webView.settings.javaScriptEnabled = true

        loginButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    loginButton.isVisible = false
                    webView.isVisible = true
                    webView.loadUrl("https://trello.com/1/authorize?expiration=never&name=EasyDone&scope=read,write&response_type=token&key=${TrelloApi.API_KEY}\n")
                }
            }
        }
        submitView.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val token = enterTokenView.text.toString()
                    val nestedBoards = api.boards(TrelloApi.API_KEY, token)
                    withContext(Dispatchers.Main) {
                        successLogin(token, nestedBoards.boards)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun successLogin(token: String, boards: List<Board>) {
        listener(token, boards)
    }

    data class Dependencies(
        val loginListener: (String, List<Board>) -> Unit,
        val api: TrelloApi
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = LoginFragment().apply {
            listener = dependencies.loginListener
            api = dependencies.api
        }
    }
}

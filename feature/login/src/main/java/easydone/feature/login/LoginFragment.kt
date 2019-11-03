package easydone.feature.login


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kamer.login.R
import easydone.library.trelloapi.TrelloApi
import easydone.library.trelloapi.model.Board
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.launch


class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var listener: (String, List<Board>) -> Unit
    private lateinit var api: TrelloApi

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        webView.settings.apply {
            javaScriptEnabled = true
            userAgentString = userAgentString.replace("; wv", "")
        }

        loginButton.setOnClickListener {
            loginButton.isVisible = false
            webView.isVisible = true
            webView.loadUrl("https://trello.com/1/authorize?expiration=never&name=EasyDone&scope=read,write&response_type=token&key=${TrelloApi.API_KEY}&callback_method=fragment&return_url=http://easydone.com")
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                request?.url?.let { url ->
                    if (url.host?.equals("easydone.com") == true) {
                        processToken(url.fragment?.substringAfter('=') ?: "")
                        return true
                    }
                }
                return false
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webView.isVisible) {
                        webView.isVisible = false
                        webView.loadUrl("about:blank")
                        webView.clearCache(true)
                        loginButton.isVisible = true
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                        isEnabled = true
                    }
                }
            }
        )
    }

    private fun processToken(token: String) {
        lifecycleScope.launch {
            try {
                val nestedBoards = api.boards(TrelloApi.API_KEY, token)
                successLogin(token, nestedBoards.boards)
            } catch (e: Exception) {
                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
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

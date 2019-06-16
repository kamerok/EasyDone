package com.kamer.login


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.kamer.login.api.TrelloApi
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginFragment : Fragment() {

    private lateinit var listener: (String) -> Unit

    private val API_KEY = "98c9ac26156a960889eb42586aa1bcd7"

    private val api: TrelloApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://trello.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrelloApi::class.java)
    }

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
                    webView.loadUrl("https://trello.com/1/authorize?expiration=never&name=EasyDone&scope=read&response_type=token&key=$API_KEY\n")
                }
            }
        }
        submitView.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val token = enterTokenView.text.toString()
                    api.me(API_KEY, token)
                    withContext(Dispatchers.Main) {
                        successLogin(token)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun successLogin(token: String) {
        listener(token)
    }

    data class Dependencies(
        val loginListener: (String) -> Unit
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = LoginFragment().apply {
            listener = dependencies.loginListener
        }
    }
}

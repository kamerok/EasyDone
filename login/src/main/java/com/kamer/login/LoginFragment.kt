package com.kamer.login


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kamer.login.api.TrelloApi
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginFragment : Fragment() {

    private val API_KEY = "98c9ac26156a960889eb42586aa1bcd7"

    private val api: TrelloApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://trello.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrelloApi::class.java)
    }

    private val loginHolder: LoginHolder by lazy { LoginHolder(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (loginHolder.hasToken()) {
            findNavController().navigate("easydone://select_board".toUri())
            return
        }

        webView.settings.javaScriptEnabled = true

        loginButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    loginButton.isVisible = false
                    webView.isVisible = true
                    webView.loadUrl("https://trello.com/1/authorize?expiration=never&name=MyPersonalToken&scope=read&response_type=token&key=$API_KEY\n")
                }
            }
        }
        submitView.setOnClickListener {
            Log.d("TAG", "onViewCreated: ${enterTokenView.text}")
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val token = enterTokenView.text.toString()
                    api.me(API_KEY, token)
                    successLogin(token)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }

    private fun successLogin(token: String) {
        loginHolder.saveToken(token)
        findNavController().navigate("easydone://select_board".toUri())
    }
}

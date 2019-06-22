package com.kamer.setupflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.kamer.login.LoginFragment
import com.kamer.selectboard.SelectBoardFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SetupFragment : Fragment() {

    private lateinit var finishListener: () -> Unit

    private var isLogin = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_setup, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startLogin()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
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
        childFragmentManager.commit {
            replace(R.id.container, LoginFragment.create(
                LoginFragment.Dependencies(
                    loginListener = { token, userId ->
                        startSelectBoard(token, userId)
                    }
                )
            ))
        }
    }

    private fun startSelectBoard(token: String, userId: String) {
        isLogin = false
        childFragmentManager.commit {
            replace(R.id.container, SelectBoardFragment.create(
                SelectBoardFragment.Dependencies(
                    token = token,
                    userId = userId,
                    listener = { boardId ->
                        GlobalScope.launch {
                            saveData(token, userId, boardId)
                            finishListener()
                        }
                    }
                )
            ))
        }
    }

    private suspend fun saveData(token: String, userId: String, boardId: String) = withContext(Dispatchers.IO) {
        //TODO: save data
    }

    data class Dependencies(
        val finishSetupListener: () -> Unit
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = SetupFragment().apply {
            finishListener = dependencies.finishSetupListener
        }
    }

}
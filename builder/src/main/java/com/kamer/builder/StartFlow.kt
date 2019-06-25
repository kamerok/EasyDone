package com.kamer.builder

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.kamer.home.HomeFragment
import com.kamer.home.HomeNavigator
import com.kamer.home.InboxTab
import com.kamer.home.Tab
import com.kamer.inbox.InboxFragment
import com.kamer.login.LoginFragment
import com.kamer.selectboard.SelectBoardFragment
import com.kamer.setupflow.R
import com.kamer.setupflow.SetupFlowNavigator
import com.kamer.setupflow.SetupFragment
import com.kamer.trelloapi.TrelloApi
import easydone.core.auth.AuthInfoHolder
import easydone.library.keyvalue.sharedprefs.SharedPrefsKeyValueStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object StartFlow {

    private lateinit var authInfoHolder: AuthInfoHolder
    private val api: TrelloApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://trello.com/1/")
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrelloApi::class.java)
    }

    fun start(activity: AppCompatActivity, containerId: Int) {
        authInfoHolder = AuthInfoHolder(SharedPrefsKeyValueStorage(activity.application, "prefs"))

        if (authInfoHolder.getToken() != null && authInfoHolder.getBoardId() != null) {
            startMainFlow(activity, containerId)
        } else {
            startSetupFlow(activity, containerId)
        }
    }

    private fun startSetupFlow(activity: AppCompatActivity, containerId: Int) {
        activity.supportFragmentManager.commit {
            var fragment: Fragment? = null
            fragment = SetupFragment.create(
                SetupFragment.Dependencies(
                    finishSetupListener = { startMainFlow(activity, containerId) },
                    navigator = object : SetupFlowNavigator {
                        override fun navigateToLogin(loginListener: (String, String) -> Unit) {
                            fragment?.run { startLogin(this, loginListener) }
                        }

                        override fun navigateToSelectBoard(token: String, userId: String, listener: (String) -> Unit) {
                            fragment?.run { startSelectBoard(this, token, userId, listener) }
                        }
                    },
                    authInfoHolder = authInfoHolder
                )
            )
            replace(containerId, fragment)
        }
    }

    private fun startMainFlow(activity: AppCompatActivity, containerId: Int) {
        var fragment: Fragment? = null
        fragment = HomeFragment.create(
            HomeFragment.Dependencies(
                tabs = listOf(InboxTab),
                navigator = object : HomeNavigator {
                    override fun navigateToTab(tab: Tab) {
                        fragment?.run {
                            this.childFragmentManager.commit {
                                replace(
                                    R.id.container, InboxFragment.create(
                                        InboxFragment.Dependencies(
                                            token = authInfoHolder.getToken()!!,
                                            boardId = authInfoHolder.getBoardId()!!,
                                            api = api
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            )
        )
        activity.supportFragmentManager.commit { replace(containerId, fragment) }
    }

    private fun startLogin(fragment: Fragment, loginListener: (String, String) -> Unit) {
        fragment.childFragmentManager.commit {
            replace(R.id.container, LoginFragment.create(LoginFragment.Dependencies(loginListener, api)))
        }
    }

    private fun startSelectBoard(fragment: Fragment, token: String, userId: String, listener: (String) -> Unit) {
        fragment.childFragmentManager.commit {
            replace(
                R.id.container, SelectBoardFragment.create(
                    SelectBoardFragment.Dependencies(
                        token = token,
                        userId = userId,
                        listener = listener,
                        api = api
                    )
                )
            )
        }
    }

}
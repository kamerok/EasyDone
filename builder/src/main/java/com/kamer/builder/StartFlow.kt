package com.kamer.builder

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.kamer.setupflow.R
import easydone.core.auth.AuthInfoHolder
import easydone.core.domain.DomainRepository
import easydone.feature.createtask.CreateTaskFragment
import easydone.feature.home.HomeFragment
import easydone.feature.home.HomeNavigator
import easydone.feature.home.InboxTab
import easydone.feature.home.TodoTab
import easydone.feature.inbox.InboxFragment
import easydone.feature.inbox.InboxNavigator
import easydone.feature.login.LoginFragment
import easydone.feature.selectboard.SelectBoardFragment
import easydone.feature.setupflow.SetupFlowNavigator
import easydone.feature.setupflow.SetupFragment
import easydone.feature.todo.TodoFragment
import easydone.feature.todo.TodoNavigator
import easydone.library.keyvalue.sharedprefs.SharedPrefsKeyValueStorage
import easydone.library.trelloapi.TrelloApi
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
    private val repository by lazy { DomainRepository(authInfoHolder, api) }

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

                        override fun navigateToSelectBoard(
                            token: String,
                            userId: String,
                            listener: (String) -> Unit
                        ) {
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
                tabs = listOf(InboxTab, TodoTab),
                fragmentFactory = { position ->
                    when (position) {
                        0 -> InboxFragment.create(
                            InboxFragment.Dependencies(
                                repository,
                                navigator = object :
                                    InboxNavigator {
                                    override fun navigateToTask(id: String) {
                                        startViewTask(id)
                                    }
                                }
                            )
                        )
                        else -> TodoFragment.create(
                            TodoFragment.Dependencies(
                                repository,
                                navigator = object : TodoNavigator {
                                    override fun navigateToTask(id: String) {
                                        startViewTask(id)
                                    }
                                }
                            )
                        )
                    }
                },
                navigator = object : HomeNavigator {
                    override fun navigateToCreate() {
                        startCreateTask()
                    }
                }
            )
        )
        activity.supportFragmentManager.commit { replace(containerId, fragment) }
    }

    private fun startLogin(
        fragment: Fragment,
        loginListener: (String, String) -> Unit
    ) {
        fragment.childFragmentManager.commit {
            replace(
                R.id.container,
                LoginFragment.create(LoginFragment.Dependencies(loginListener, api))
            )
        }
    }

    private fun startSelectBoard(
        fragment: Fragment,
        token: String,
        userId: String,
        listener: (String) -> Unit
    ) {
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

    private fun startViewTask(id: String) {
        /*fragment!!.childFragmentManager.commit {
            replace(
                R.id.container,
                EditTaskFragment.create(
                    EditTaskFragment.Dependencies(
                        id = id,
                        boardId = authInfoHolder.getBoardId()!!,
                        token = authInfoHolder.getToken()!!,
                        api = api
                    )
                )
            )
        }*/
    }

    private fun startCreateTask() {
        /*fragment?.run {
            this.childFragmentManager.commit {
                replace(
                    R.id.container,
                    CreateTaskFragment.create(
                        CreateTaskFragment.Dependencies(
                            token = authInfoHolder.getToken()!!,
                            boardId = authInfoHolder.getBoardId()!!,
                            api = api
                        )
                    )
                )
            }
        }*/
    }

}
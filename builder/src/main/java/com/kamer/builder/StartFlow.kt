package com.kamer.builder

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.kamer.setupflow.R
import easydone.core.auth.AuthInfoHolder
import easydone.core.domain.DomainRepository
import easydone.feature.createtask.CreateTaskFragment
import easydone.feature.edittask.EditTaskFragment
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

    private lateinit var navigator: Navigator

    private lateinit var application: Application
    private val authInfoHolder by lazy {
        AuthInfoHolder(SharedPrefsKeyValueStorage(application, "prefs"))
    }
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
        application = activity.application
        navigator = Navigator(activity.supportFragmentManager, containerId)

        if (authInfoHolder.getToken() != null && authInfoHolder.getBoardId() != null) {
            startMainFlow()
        } else {
            startSetupFlow()
        }
    }

    private fun startSetupFlow() {
        var localNavigator: Navigator? = null
        val fragment = SetupFragment.create(
            SetupFragment.Dependencies(
                finishSetupListener = { startMainFlow() },
                navigator = object : SetupFlowNavigator {
                    override fun navigateToLogin(loginListener: (String, String) -> Unit) {
                        localNavigator?.openScreen(
                            LoginFragment.create(LoginFragment.Dependencies(loginListener, api))
                        )
                    }

                    override fun navigateToSelectBoard(
                        token: String,
                        userId: String,
                        listener: (String) -> Unit
                    ) {
                        localNavigator?.openScreen(
                            SelectBoardFragment.create(
                                SelectBoardFragment.Dependencies(
                                    token = token,
                                    userId = userId,
                                    listener = listener,
                                    api = api
                                )
                            )
                        )
                    }
                },
                authInfoHolder = authInfoHolder
            )
        )
        navigator.openScreen(fragment)
        localNavigator = Navigator(fragment.childFragmentManager, R.id.container)
    }

    private fun startMainFlow() {
        navigator.openScreen(HomeFragment.create(
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
        ))
    }

    private fun startViewTask(id: String) {
        navigator.openScreen(
            EditTaskFragment.create(
                EditTaskFragment.Dependencies(
                    id = id,
                    boardId = authInfoHolder.getBoardId()!!,
                    token = authInfoHolder.getToken()!!,
                    api = api
                )
            )
        )
    }

    private fun startCreateTask() {
        navigator.openScreen(
            CreateTaskFragment.create(
                CreateTaskFragment.Dependencies(
                    token = authInfoHolder.getToken()!!,
                    boardId = authInfoHolder.getBoardId()!!,
                    api = api
                )
            )
        )
    }

}
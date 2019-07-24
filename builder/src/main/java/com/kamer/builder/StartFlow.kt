package com.kamer.builder

import android.app.Application
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kamer.setupflow.R
import easydone.core.database.DatabaseImpl
import easydone.core.database.MyDatabase
import easydone.core.domain.DomainRepository
import easydone.core.domain.Synchronizer
import easydone.core.network.AuthInfoHolder
import easydone.core.network.Network
import easydone.feature.createtask.CreateTaskFragment
import easydone.feature.createtask.CreateTaskNavigator
import easydone.feature.edittask.EditTaskFragment
import easydone.feature.edittask.EditTaskNavigator
import easydone.feature.home.HomeFragment
import easydone.feature.home.HomeNavigator
import easydone.feature.home.InboxTab
import easydone.feature.home.TodoTab
import easydone.feature.inbox.InboxFragment
import easydone.feature.inbox.InboxNavigator
import easydone.feature.login.LoginFragment
import easydone.feature.selectboard.BoardUiModel
import easydone.feature.selectboard.SelectBoardFragment
import easydone.feature.settings.SettingsFragment
import easydone.feature.settings.SettingsNavigator
import easydone.feature.setupflow.SetupFlowNavigator
import easydone.feature.setupflow.SetupFragment
import easydone.feature.todo.TodoFragment
import easydone.feature.todo.TodoNavigator
import easydone.library.keyvalue.sharedprefs.SharedPrefsKeyValueStorage
import easydone.library.navigation.Navigator
import easydone.library.trelloapi.TrelloApi
import easydone.library.trelloapi.model.Board


object StartFlow {

    private lateinit var navigator: Navigator

    private lateinit var application: Application
    private val authInfoHolder by lazy {
        AuthInfoHolder(SharedPrefsKeyValueStorage(application, "prefs"))
    }
    private val api: TrelloApi by lazy { TrelloApi.build() }
    private val database: MyDatabase by lazy { DatabaseImpl(application) }
    private val network: Network by lazy {
        Network(api, authInfoHolder, SharedPrefsKeyValueStorage(application, "id_mapping"))
    }
    private val synchronizer by lazy { Synchronizer(network, database) }
    private val repository by lazy { DomainRepository(database) }

    fun start(activity: AppCompatActivity, containerId: Int) {
        application = activity.application
        navigator = Navigator(activity.supportFragmentManager, containerId)
        activity.onBackPressedDispatcher.addCallback(
            activity,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!navigator.isEmpty()) {
                        navigator.popScreen()
                    } else {
                        isEnabled = false
                        activity.onBackPressed()
                        isEnabled = true
                    }
                }
            }
        )

        startInitialFlow()
    }

    fun startCreate(activity: AppCompatActivity, containerId: Int) {
        application = activity.application
        navigator = Navigator(activity.supportFragmentManager, containerId)

        navigator.openScreen(
            CreateTaskFragment.create(CreateTaskFragment.Dependencies(
                repository,
                object : CreateTaskNavigator {
                    override fun closeScreen() {
                        activity.finish()
                    }
                }
            )),
            false
        )
    }

    private fun startInitialFlow() {
        navigator.clearStack()
        if (authInfoHolder.getToken() != null && authInfoHolder.getBoardId() != null) {
            startMainFlow()
        } else {
            startSetupFlow()
        }
    }

    private fun startSetupFlow() {
        var fragment: Fragment? = null
        val localNavigator: Navigator by lazy {
            Navigator(
                fragment!!.childFragmentManager,
                R.id.container
            )
        }
        fragment = SetupFragment.create(
            SetupFragment.Dependencies(
                finishSetupListener = { startMainFlow() },
                navigator = object : SetupFlowNavigator {
                    override fun navigateToLogin(loginListener: (String, List<Board>) -> Unit) {
                        localNavigator.openScreen(
                            LoginFragment.create(LoginFragment.Dependencies(loginListener, api))
                        )
                    }

                    override fun navigateToSelectBoard(
                        boards: List<Board>,
                        listener: (String) -> Unit
                    ) {
                        localNavigator.openScreen(
                            SelectBoardFragment.create(
                                SelectBoardFragment.Dependencies(
                                    boards = boards.map { BoardUiModel(it.id, it.name) },
                                    listener = listener
                                )
                            )
                        )
                    }
                },
                authInfoHolder = authInfoHolder
            )
        )
        navigator.openScreen(fragment)
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
                repository = repository,
                synchronizer = synchronizer,
                navigator = object : HomeNavigator {
                    override fun navigateToCreate() {
                        startCreateTask()
                    }

                    override fun navigateToSettings() {
                        startSettings()
                    }
                }
            )
        ))
    }

    private fun startViewTask(id: String) {
        navigator.openScreen(
            EditTaskFragment.create(EditTaskFragment.Dependencies(
                id,
                repository,
                object : EditTaskNavigator {
                    override fun closeScreen() {
                        navigator.popScreen()
                    }
                }
            )),
            true
        )
    }

    fun startCreateTask() {
        navigator.openScreen(
            CreateTaskFragment.create(CreateTaskFragment.Dependencies(
                repository,
                object : CreateTaskNavigator {
                    override fun closeScreen() {
                        navigator.popScreen()
                    }
                }
            )),
            true
        )
    }

    private fun startSettings() {
        navigator.openScreen(
            SettingsFragment.create(
                SettingsFragment.Dependencies(
                    authInfoHolder,
                    object : SettingsNavigator {
                        override fun navigateToSetup() {
                            startInitialFlow()
                        }
                    }
                )
            ),
            true
        )
    }

}
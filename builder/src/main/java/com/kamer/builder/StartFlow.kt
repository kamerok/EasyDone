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
import easydone.feature.feed.FeedFragment
import easydone.feature.feed.FeedNavigator
import easydone.feature.home.FragmentFactory
import easydone.feature.home.HomeFragment
import easydone.feature.home.HomeNavigator
import easydone.feature.login.LoginFragment
import easydone.feature.quickcreatetask.QuickCreateTaskFragment
import easydone.feature.quickcreatetask.QuickCreateTaskNavigator
import easydone.feature.selectboard.BoardUiModel
import easydone.feature.selectboard.SelectBoardFragment
import easydone.feature.settings.SettingsFragment
import easydone.feature.settings.SettingsNavigator
import easydone.feature.setupflow.SetupFlowNavigator
import easydone.feature.setupflow.SetupFragment
import easydone.library.keyvalue.sharedprefs.SharedPrefsKeyValueStorage
import easydone.library.navigation.Navigator
import easydone.library.trelloapi.TrelloApi
import easydone.library.trelloapi.model.Board
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module


object StartFlow {

    private lateinit var navigator: Navigator

    var fragment: Fragment? = null
    val localNavigator: Navigator by lazy {
        Navigator(
            fragment!!.childFragmentManager,
            R.id.container
        )
    }

    fun start(activity: AppCompatActivity, containerId: Int) {
        ActivityHolder.setActivity(activity)
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
        ActivityHolder.setActivity(activity)
        navigator = Navigator(activity.supportFragmentManager, containerId)

        navigator.openScreen(QuickCreateTaskFragment.create(), false)

        //to start syncing
        GlobalContext.get().koin.get<Synchronizer>()
    }

    fun initDependencies(application: Application) {
        val module = module {
            single { DomainRepository(get()) }
            single { Synchronizer(get(), get()) }
            single { AuthInfoHolder(SharedPrefsKeyValueStorage(get(), "prefs")) }
            single { Network(get(), get(), SharedPrefsKeyValueStorage(application, "id_mapping")) }
            single { TrelloApi.build() }
            single<MyDatabase> { DatabaseImpl(get()) }
            factory<FragmentFactory> {
                object : FragmentFactory {
                    override fun create(): Fragment = FeedFragment.create()
                }
            }
            factory<HomeNavigator> {
                object : HomeNavigator {
                    override fun navigateToCreate() {
                        startCreateTask()
                    }

                    override fun navigateToSettings() {
                        startSettings()
                    }
                }
            }
            factory<FeedNavigator> {
                object : FeedNavigator {
                    override fun navigateToTask(id: String) {
                        startViewTask(id)
                    }
                }
            }
            factory<QuickCreateTaskNavigator> {
                object : QuickCreateTaskNavigator {
                    override fun closeScreen() {
                        ActivityHolder.getActivity().finishAffinity()
                    }
                }
            }
            factory<EditTaskNavigator> {
                object : EditTaskNavigator {
                    override fun closeScreen() {
                        navigator.popScreen()
                    }
                }
            }
            factory<CreateTaskNavigator> {
                object : CreateTaskNavigator {
                    override fun closeScreen() {
                        navigator.popScreen()
                    }
                }
            }
            factory<SettingsNavigator> {
                object : SettingsNavigator {
                    override fun navigateToSetup() {
                        startInitialFlow()
                    }
                }
            }
            factory<SetupFlowNavigator> {
                object : SetupFlowNavigator {
                    override fun navigateToLogin(loginListener: (String, List<Board>) -> Unit) {
                        localNavigator.openScreen(
                            LoginFragment.create(LoginFragment.Dependencies(loginListener, get()))
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

                    override fun onFinishSetup() = startMainFlow()
                }
            }
        }
        startKoin {
            androidContext(application)
            modules(module)
        }
    }

    private fun startInitialFlow() {
        val authInfoHolder: AuthInfoHolder = GlobalContext.get().koin.get()
        navigator.clearStack()
        if (authInfoHolder.getToken() != null && authInfoHolder.getBoardId() != null) {
            startMainFlow()
        } else {
            startSetupFlow()
        }
    }

    private fun startSetupFlow() {
        fragment = SetupFragment.create()
        navigator.openScreen(fragment!!)
    }

    private fun startMainFlow() = navigator.openScreen(HomeFragment.create())

    private fun startViewTask(id: String) = navigator.openScreen(EditTaskFragment.create(id), true)

    private fun startCreateTask() = navigator.openScreen(CreateTaskFragment.create(), true)

    private fun startSettings() = navigator.openScreen(SettingsFragment.create(), true)

}

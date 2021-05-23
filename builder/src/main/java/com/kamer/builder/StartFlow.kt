package com.kamer.builder

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
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
import easydone.feature.feed.FeedViewModel
import easydone.feature.home.HomeFragment
import easydone.feature.home.HomeNavigator
import easydone.feature.login.LoginFragment
import easydone.feature.login.TokenProvider
import easydone.feature.quickcreatetask.QuickCreateTaskFragment
import easydone.feature.quickcreatetask.QuickCreateTaskNavigator
import easydone.feature.selectboard.BoardUiModel
import easydone.feature.selectboard.SelectBoardFragment
import easydone.feature.settings.SettingsFragment
import easydone.feature.settings.SettingsNavigator
import easydone.feature.setupflow.SetupFlowNavigator
import easydone.feature.setupflow.SetupFragment
import easydone.library.keyvalue.sharedprefs.SharedPrefsKeyValueStorage
import easydone.library.navigation.FragmentManagerNavigator
import easydone.library.navigation.Navigator
import easydone.library.trelloapi.TrelloApi
import easydone.library.trelloapi.model.Board
import kotlinx.coroutines.flow.Flow
import okhttp3.Interceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module


object StartFlow {

    init {
        Features.registries[Feature.FEED] = object : FeatureRegistry {
            override val featureClass: Class<out Fragment> = FeedFragment::class.java

            override fun create(): Fragment =
                FeedFragment { fragment ->
                    ViewModelProvider(
                        fragment,
                        object : AbstractSavedStateViewModelFactory(fragment, null) {
                            override fun <T : ViewModel?> create(
                                key: String,
                                modelClass: Class<T>,
                                handle: SavedStateHandle
                            ): T =
                                FeedViewModel(
                                    GlobalContext.get().koin.get(),
                                    object : FeedNavigator {
                                        override fun navigateToTask(id: String) {
                                            GlobalContext.get().koin.get<DeepLinkNavigator>()
                                                .execute(NavigationCommand.EditTask(id))
                                        }
                                    }
                                ) as T
                        }).get()
                }
        }
    }

    fun start() {
        startInitialFlow()
    }

    fun startCreate() {
        GlobalContext.get().koin.get<Navigator>().openScreen(QuickCreateTaskFragment::class.java)

        //to start syncing
        GlobalContext.get().koin.get<Synchronizer>()
    }

    fun initDependencies(application: Application, debugInterceptor: Interceptor?) {
        val module = module {
            single { DomainRepository(get()) }
            single { Synchronizer(get(), get()) }
            single { AuthInfoHolder(SharedPrefsKeyValueStorage(get(), "prefs")) }
            single { Network(get(), get(), SharedPrefsKeyValueStorage(application, "id_mapping")) }
            single { TrelloApi.build(debugInterceptor) }
            single<MyDatabase> { DatabaseImpl(get()) }
            single { ActivityNavigator() }
            single { get<ActivityNavigator>() as Navigator }
            single { DeepLinkResolver() }
            single {
                object : TokenProvider {
                    override fun observeToken(): Flow<String> =
                        get<DeepLinkResolver>().observeToken()
                } as TokenProvider
            }
            single {
                object : DeepLinkNavigator {
                    override fun execute(command: NavigationCommand) {
                        when (command) {
                            is NavigationCommand.EditTask -> startViewTask(command.id, get())
                        }
                    }
                } as DeepLinkNavigator
            }
        }
        val fragmentModule = module {
            factory {
                HomeFragment(
                    Features.registries[Feature.FEED]!!.featureClass,
                    get(),
                    object : HomeNavigator {
                        override fun navigateToCreate() {
                            startCreateTask(get())
                        }

                        override fun navigateToSettings() {
                            startSettings(get())
                        }
                    }
                )
            }
            factory {
                EditTaskFragment(
                    get(),
                    object : EditTaskNavigator {
                        override fun closeScreen() {
                            get<Navigator>().popScreen()
                        }
                    }
                )
            }
            factory {
                QuickCreateTaskFragment(
                    get(),
                    object : QuickCreateTaskNavigator {
                        override fun closeScreen() {
                            ActivityHolder.getActivity().finishAffinity()
                        }
                    }
                )
            }
            factory {
                CreateTaskFragment(
                    get(),
                    object : CreateTaskNavigator {
                        override fun closeScreen() {
                            get<Navigator>().popScreen()
                        }
                    }
                )
            }
            factory {
                SettingsFragment(
                    get(),
                    object : SettingsNavigator {
                        override fun navigateToSetup() {
                            startInitialFlow()
                        }
                    }
                )
            }
            factory {
                var fragment: SetupFragment? = null
                val localNavigator: Navigator by lazy {
                    FragmentManagerNavigator(
                        fragment!!.childFragmentManager,
                        R.id.container
                    )
                }
                fragment = SetupFragment(
                    get(),
                    object : SetupFlowNavigator {
                        override fun navigateToLogin(loginListener: (String, List<Board>) -> Unit) {
                            localNavigator.openScreen(
                                LoginFragment.create(
                                    LoginFragment.Dependencies(
                                        loginListener,
                                        get(),
                                        get()
                                    )
                                )
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

                        override fun onFinishSetup() = startMainFlow(get())
                    }
                )
                fragment
            }
        }
        startKoin {
            androidContext(application)
            modules(listOf(module, fragmentModule))
        }
    }

    private fun startInitialFlow() {
        val authInfoHolder: AuthInfoHolder = GlobalContext.get().koin.get()
        val navigator: Navigator = GlobalContext.get().koin.get()
        navigator.clearStack()
        if (authInfoHolder.getToken() != null && authInfoHolder.getBoardId() != null) {
            startMainFlow(navigator)
        } else {
            startSetupFlow(navigator)
        }
    }

    private fun startSetupFlow(navigator: Navigator) {
        navigator.openScreen(SetupFragment::class.java)
    }

    private fun startMainFlow(navigator: Navigator) = navigator.openScreen(HomeFragment::class.java)

    private fun startViewTask(id: String, navigator: Navigator) =
        navigator.openScreen(
            EditTaskFragment::class.java,
            true,
            Bundle().apply { putString(EditTaskFragment.TASK_ID, id) }
        )

    private fun startCreateTask(navigator: Navigator) =
        navigator.openScreen(CreateTaskFragment::class.java, true)

    private fun startSettings(navigator: Navigator) =
        navigator.openScreen(SettingsFragment::class.java, true)

}

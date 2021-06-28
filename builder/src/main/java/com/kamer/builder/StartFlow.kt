package com.kamer.builder

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
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
import easydone.feature.quickcreatetask.QuickCreateTaskFragment
import easydone.feature.quickcreatetask.QuickCreateTaskNavigator
import easydone.feature.settings.SettingsFragment
import easydone.feature.settings.SettingsNavigator
import easydone.feature.setupflow.SetupFragment
import easydone.library.keyvalue.sharedprefs.SharedPrefsKeyValueStorage
import easydone.library.navigation.Navigator
import easydone.library.trelloapi.TrelloApi
import okhttp3.Interceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module


object StartFlow {

    fun start() {
        startInitialFlow()
    }

    fun startCreate() {
        GlobalContext.get().get<Navigator>().openScreen(QuickCreateTaskFragment::class.java)

        //to start syncing
        GlobalContext.get().get<Synchronizer>()
    }

    fun initDependencies(
        application: Application,
        trelloApiKey: String,
        debugInterceptor: Interceptor?
    ) {
        val module = module {
            single { DomainRepository(get()) }
            single { Synchronizer(get(), get()) }
            single { AuthInfoHolder(SharedPrefsKeyValueStorage(get(), "prefs")) }
            single {
                Network(
                    api = get(),
                    apiKey = trelloApiKey,
                    authInfoHolder = get(),
                    idMappings = SharedPrefsKeyValueStorage(application, "id_mapping")
                )
            }
            single { TrelloApi.build(debugInterceptor) }
            single<MyDatabase> { DatabaseImpl(get()) }
            single { ActivityNavigator() }
            single { get<ActivityNavigator>() as Navigator }
            single { DeepLinkResolver() }
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
                    FeedFragment::class.java,
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
                                    GlobalContext.get().get(),
                                    object : FeedNavigator {
                                        override fun navigateToTask(id: String) {
                                            GlobalContext.get().get<DeepLinkNavigator>()
                                                .execute(NavigationCommand.EditTask(id))
                                        }
                                    }
                                ) as T
                        }).get()
                }
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
                SetupFragment(
                    get(),
                    get(),
                    trelloApiKey,
                    get<DeepLinkResolver>().observeToken()
                ) { startMainFlow(get()) }
            }
        }
        startKoin {
            androidContext(application)
            modules(listOf(module, fragmentModule))
        }
    }

    private fun startInitialFlow() {
        val authInfoHolder: AuthInfoHolder = GlobalContext.get().get()
        val navigator: Navigator = GlobalContext.get().get()
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

package com.kamer.builder

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.sqldelight.android.AndroidSqliteDriver
import easydone.core.database.Database
import easydone.core.database.DatabaseLocalDataSource
import easydone.core.domain.DomainRepository
import easydone.core.domain.LocalDataSource
import easydone.core.domain.RemoteDataSource
import easydone.core.domain.Synchronizer
import easydone.core.network.AuthInfoHolder
import easydone.core.network.TrelloRemoteDataSource
import easydone.feature.edittask.EditTaskFragment
import easydone.feature.edittask.EditTaskNavigator
import easydone.feature.home.HomeFragment
import easydone.feature.home.HomeNavigator
import easydone.feature.inbox.InboxFragment
import easydone.feature.inbox.InboxNavigator
import easydone.feature.quickcreatetask.QuickCreateTaskFragment
import easydone.feature.quickcreatetask.QuickCreateTaskNavigator
import easydone.feature.settings.SettingsFragment
import easydone.feature.settings.SettingsNavigator
import easydone.feature.setupflow.SetupFragment
import easydone.feature.taskdetails.TaskDetailsFragment
import easydone.feature.taskdetails.TaskDetailsNavigator
import easydone.feature.waiting.WaitingFragment
import easydone.feature.waiting.WaitingNavigator
import easydone.library.keyvalue.sharedprefs.DataStoreKeyValueStorage
import easydone.library.navigation.Navigator
import easydone.library.trelloapi.TrelloApi
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

val Context.prefsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "prefs",
    produceMigrations = { context -> listOf(SharedPreferencesMigration(context, "prefs")) }
)
val Context.mappingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "id_mapping",
    produceMigrations = { context -> listOf(SharedPreferencesMigration(context, "id_mapping")) }
)

object StartFlow {

    fun start(isSandbox: Boolean) {
        if (isSandbox) {
            GlobalContext.get().get<LocalDataSourceDecorator>().switchToSandbox()
            GlobalContext.get().get<RemoteDataSourceDecorator>().switchToSandbox()
        }
        runBlocking { startInitialFlow() }
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
            single {
                AuthInfoHolder(DataStoreKeyValueStorage(get<Application>().prefsDataStore))
            }
            single {
                TrelloRemoteDataSource(
                    api = get(),
                    apiKey = trelloApiKey,
                    authInfoHolder = get(),
                    idMappings = DataStoreKeyValueStorage(get<Application>().mappingsDataStore)
                )
            }
            single { RemoteDataSourceDecorator(get<TrelloRemoteDataSource>()) }
            single<RemoteDataSource> { get<RemoteDataSourceDecorator>() }
            single { TrelloApi.build(debugInterceptor) }
            single {
                DatabaseLocalDataSource(
                    AndroidSqliteDriver(Database.Schema, application, "database.db")
                )
            }
            single { LocalDataSourceDecorator(get<DatabaseLocalDataSource>()) }
            single<LocalDataSource> { get<LocalDataSourceDecorator>() }
            single { ActivityNavigator() }
            single<Navigator> { get<ActivityNavigator>() }
            single { DeepLinkResolver() }
        }
        val fragmentModule = module {
            factory {
                HomeFragment(
                    get(),
                    get(),
                    object : HomeNavigator {
                        override fun navigateToCreate() {
                            startCreateTask(get())
                        }

                        override fun navigateToSettings() {
                            startSettings(get())
                        }

                        override fun navigateToInbox() {
                            get<Navigator>().openScreen(InboxFragment::class.java, true)
                        }

                        override fun navigateToWaiting() {
                            get<Navigator>().openScreen(WaitingFragment::class.java, true)
                        }

                        override fun navigateToTask(id: String) {
                            startViewTask(id, get())
                        }
                    }
                )
            }
            factory {
                InboxFragment(
                    get(),
                    object : InboxNavigator {
                        override fun openTask(id: String) {
                            startViewTask(id, get())
                        }

                        override fun close() {
                            get<Navigator>().popScreen()
                        }
                    }
                )
            }
            factory {
                WaitingFragment(
                    get(),
                    object : WaitingNavigator {
                        override fun openTask(id: String) {
                            startViewTask(id, get())
                        }
                    }
                )
            }
            factory {
                TaskDetailsFragment(
                    get(),
                    object : TaskDetailsNavigator {
                        private val navigator = get<Navigator>()

                        override fun editTask(id: String) {
                            navigator.openScreen(
                                fragmentClass = EditTaskFragment::class.java,
                                addToBackStack = true,
                                args = EditTaskFragment.createArgs(id)
                            )
                        }

                        override fun close() {
                            navigator.popScreen()
                        }
                    }
                )
            }
            factory {
                EditTaskFragment(
                    get(),
                    object : EditTaskNavigator {
                        override fun close() {
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
                SettingsFragment(
                    get(),
                    object : SettingsNavigator {
                        override fun navigateToSetup() {
                            runBlocking { startInitialFlow() }
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

    private suspend fun startInitialFlow() {
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
            TaskDetailsFragment::class.java,
            true,
            TaskDetailsFragment.createArgs(id)
        )

    private fun startCreateTask(navigator: Navigator) =
        navigator.openScreen(EditTaskFragment::class.java, true)

    private fun startSettings(navigator: Navigator) =
        navigator.openScreen(SettingsFragment::class.java, true)

}

package com.kamer.builder

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.squareup.sqldelight.android.AndroidSqliteDriver
import easydone.core.database.Database
import easydone.core.database.DatabaseFactory
import easydone.core.database.DatabaseLocalDataSource
import easydone.core.domain.DomainRepository
import easydone.core.domain.LocalDataSource
import easydone.core.domain.RemoteDataSource
import easydone.core.domain.SyncDelegate
import easydone.core.domain.SyncScheduler
import easydone.core.domain.Synchronizer
import easydone.feature.edittask.EditTaskFragment
import easydone.feature.edittask.EditTaskNavigator
import easydone.feature.quickcreatetask.QuickCreateTaskFragment
import easydone.feature.quickcreatetask.QuickCreateTaskNavigator
import easydone.feature.settings.SettingsNavigator
import easydone.library.keyvalue.sharedprefs.DataStoreKeyValueStorage
import easydone.library.navigation.Navigator
import easydone.service.trello.TrelloRemoteDataSource
import easydone.service.trello.api.TrelloApi
import easydone.widget.updateWidget
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    fun start(isSandbox: Boolean, isInboxDeeplink: Boolean) {
        if (isSandbox) {
            GlobalContext.get().get<LocalDataSourceDecorator>().switchToSandbox()
            GlobalContext.get().get<RemoteDataSourceDecorator>().switchToSandbox()
        }
        runBlocking { startInitialFlow(isInboxDeeplink) }
    }

    fun startQuickCreate() {
        GlobalContext.get().get<Navigator>().openScreen(QuickCreateTaskFragment::class.java)

        //to start syncing
        GlobalContext.get().get<SyncScheduler>()
    }

    fun startCreate(sharedText: String) {
        GlobalContext.get().get<Navigator>().openScreen(
            fragmentClass = EditTaskFragment::class.java,
            addToBackStack = false,
            args = EditTaskFragment.createArgs(sharedText)
        )

        //to start syncing
        GlobalContext.get().get<SyncScheduler>()
    }

    fun initDependencies(
        application: Application,
        trelloApiKey: String,
        debugInterceptor: Interceptor?
    ) {
        val module = module {
            single { DomainRepository(get()) }
            single<SyncDelegate> { WorkManagerSyncDelegate(get()) }
            single { Synchronizer(get(), get()) }
            single { SyncScheduler(get(), get()) }
            single {
                TrelloRemoteDataSource(
                    api = get(),
                    apiKey = trelloApiKey,
                    prefs = DataStoreKeyValueStorage(get<Application>().prefsDataStore),
                    idMappings = DataStoreKeyValueStorage(get<Application>().mappingsDataStore)
                )
            }
            single { RemoteDataSourceDecorator(get<TrelloRemoteDataSource>()) }
            single<RemoteDataSource> { get<RemoteDataSourceDecorator>() }
            single { TrelloApi.build(debugInterceptor) }
            single {
                DatabaseLocalDataSource(
                    DatabaseFactory.create(
                        AndroidSqliteDriver(Database.Schema, application, "database.db")
                    )
                )
            }
            single { LocalDataSourceDecorator(get<DatabaseLocalDataSource>()) }
            single<LocalDataSource> { get<LocalDataSourceDecorator>() }
            single { ActivityNavigator() }
            single<Navigator> { get<ActivityNavigator>() }
        }
        val fragmentModule = module {
            factory {
                MainNavigationFragment(
                    get(),
                    get(),
                    get(),
                    object : SettingsNavigator {
                        override fun navigateToSetup() {
                            runBlocking { startInitialFlow() }
                        }
                    },
                    get(),
                    get(),
                    trelloApiKey,
                )
            }
            factory {
                EditTaskFragment(
                    get(),
                    object : EditTaskNavigator {
                        override fun close() {
                            val navigator = get<Navigator>()
                            if (!navigator.isEmpty()) {
                                navigator.popScreen()
                            } else {
                                ActivityHolder.getActivity().onBackPressedDispatcher.onBackPressed()
                            }
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
        }
        startKoin {
            androidContext(application)
            modules(listOf(module, fragmentModule))
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun startWidgetUpdates() {
        val repo = GlobalContext.get().get<DomainRepository>()
        val context = GlobalContext.get().get<Context>()
        repo.getAllTasks()
            .onEach { updateWidget(context) }
            .launchIn(GlobalScope)
    }

    fun workManagerConfiguration(): Configuration = Configuration.Builder()
        .setWorkerFactory(object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters
            ): ListenableWorker? {
                if (workerClassName == SyncWorker::class.java.name) {
                    return SyncWorker(
                        appContext = appContext,
                        workerParams = workerParameters,
                        synchronizer = GlobalContext.get().get()
                    )
                }
                return null
            }
        })
        .build()

    private suspend fun startInitialFlow(isInboxDeeplink: Boolean = false) {
        val remoteDataSource: RemoteDataSource = GlobalContext.get().get()
        val navigator: Navigator = GlobalContext.get().get()
        navigator.clearStack()
        if (remoteDataSource.isConnected()) {
            startMainFlow(navigator, isInboxDeeplink)
        } else {
            startMainFlow(navigator)
        }
    }

    private fun startMainFlow(
        navigator: Navigator,
        isInboxDeeplink: Boolean = false
    ) = if (isInboxDeeplink) {
        // TODO: start inbox
        navigator.setupScreenStack(MainNavigationFragment::class.java)
    } else {
        navigator.openScreen(MainNavigationFragment::class.java)
    }

}

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
import easydone.library.keyvalue.sharedprefs.DataStoreKeyValueStorage
import easydone.library.navigation.Navigator
import easydone.service.trello.TrelloRemoteDataSource
import easydone.service.trello.api.TrelloApi
import easydone.widget.updateWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    fun enableSandbox() {
        GlobalContext.get().get<LocalDataSourceDecorator>().switchToSandbox()
        GlobalContext.get().get<RemoteDataSourceDecorator>().switchToSandbox()
    }

    fun startSyncing() {
        GlobalContext.get().get<SyncScheduler>()
    }

    fun initDependencies(
        application: Application,
        applicationScope: CoroutineScope,
        trelloApiKey: String,
        debugInterceptor: Interceptor?
    ) {
        val module = module {
            single { DomainRepository(get()) }
            single<SyncDelegate> { WorkManagerSyncDelegate(get()) }
            single { Synchronizer(get(), get()) }
            single { SyncScheduler(get(), get(), applicationScope) }
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
        startKoin {
            androidContext(application)
            modules(listOf(module))
        }
    }

    fun startWidgetUpdates(scope: CoroutineScope) {
        val repo = GlobalContext.get().get<DomainRepository>()
        val context = GlobalContext.get().get<Context>()
        repo.getAllTasks()
            .onEach { updateWidget(context) }
            .launchIn(scope)
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

}

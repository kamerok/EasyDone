package easydone.app

import android.app.Application
import androidx.work.Configuration
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kamer.builder.StartFlow
import com.kamer.builder.WorkScheduler
import com.kamer.easydone.BuildConfig
import kotlinx.coroutines.MainScope
import okhttp3.Interceptor
import timber.log.LogcatTree
import timber.log.Timber


class App : Application(), Configuration.Provider {

    private val applicationScope = MainScope()

    override fun onCreate() {
        super.onCreate()
        var debugInterceptor: Interceptor? = null
        if (BuildConfig.DEBUG) {
            debugInterceptor = FlipperInitializer.init(this)
            Timber.plant(LogcatTree())
        }
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        StartFlow.initDependencies(
            application = this,
            applicationScope = applicationScope,
            trelloApiKey = BuildConfig.TRELLO_API_KEY,
            debugInterceptor = debugInterceptor
        )
        StartFlow.startSyncing()
        StartFlow.startWidgetUpdates(applicationScope)
        WorkScheduler.schedulePeriodicSync(this)
    }

    override val workManagerConfiguration: Configuration
        get() = StartFlow.workManagerConfiguration()
}

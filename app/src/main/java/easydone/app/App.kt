package easydone.app

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kamer.builder.StartFlow
import com.kamer.easydone.BuildConfig
import okhttp3.Interceptor
import timber.log.LogcatTree
import timber.log.Timber


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        var debugInterceptor: Interceptor? = null
        if (BuildConfig.DEBUG) {
            debugInterceptor = FlipperInitializer.init(this)
            Timber.plant(LogcatTree())
        }
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        StartFlow.initDependencies(this, BuildConfig.TRELLO_API_KEY, debugInterceptor)
        StartFlow.startWidgetUpdates()
    }
}

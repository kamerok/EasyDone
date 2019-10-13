package easydone.app

import android.app.Application
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import com.kamer.builder.StartFlow
import com.kamer.easydone.BuildConfig
import timber.log.LogcatTree
import timber.log.Timber
import timber.log.Tree


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
            Timber.plant(LogcatTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
        StartFlow.initDependencies(this)
    }

    private class CrashReportingTree : Tree() {
        override fun performLog(
            priority: Int,
            tag: String?,
            throwable: Throwable?,
            message: String?
        ) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }

            Crashlytics.log(priority, tag, message)

            if (throwable != null) {
                if (priority == Log.ERROR) {
                    Crashlytics.logException(throwable)
                } else if (priority == Log.WARN) {
                    Crashlytics.logException(throwable)
                }
            }
        }
    }
}

package easydone.app

import android.app.Application
import android.util.Log
import androidx.annotation.NonNull
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import com.kamer.easydone.BuildConfig
import timber.log.Timber


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, @NonNull message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }

            Crashlytics.log(priority, tag, message)

            if (t != null) {
                if (priority == Log.ERROR) {
                    Crashlytics.logException(t)
                } else if (priority == Log.WARN) {
                    Crashlytics.logException(t)
                }
            }
        }
    }
}
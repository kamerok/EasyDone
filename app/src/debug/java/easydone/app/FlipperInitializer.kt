package easydone.app

import android.app.Application
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.navigation.NavigationFlipperPlugin
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import okhttp3.Interceptor


object FlipperInitializer {
    fun init(application: Application): Interceptor? =
        if (FlipperUtils.shouldEnableFlipper(application)) {
            SoLoader.init(application, false)
            val networkFlipperPlugin = NetworkFlipperPlugin()
            AndroidFlipperClient.getInstance(application).apply {
                addPlugin(InspectorFlipperPlugin(application, DescriptorMapping.withDefaults()))
                addPlugin(DatabasesFlipperPlugin(application))
                addPlugin(SharedPreferencesFlipperPlugin(application))
                addPlugin(networkFlipperPlugin)
                addPlugin(NavigationFlipperPlugin.getInstance())
            }.start()
            FlipperOkhttpInterceptor(networkFlipperPlugin)
        } else {
            null
        }
}

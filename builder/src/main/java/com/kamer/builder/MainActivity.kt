package com.kamer.builder

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kamer.builder.ActivityHolder
import com.kamer.builder.ActivityNavigator
import com.kamer.builder.CustomFragmentFactory
import com.kamer.builder.DeepLinkResolver
import com.kamer.builder.StartFlow
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity(R.layout.activity_container) {

    private val navigator: ActivityNavigator by inject()
    private val deepLinkResolver: DeepLinkResolver by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = CustomFragmentFactory
        var flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        if (resources.getBoolean(R.bool.light_system_bars)) {
            flags = flags or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        window.decorView.systemUiVisibility = flags

        super.onCreate(savedInstanceState)
        window.setBackgroundDrawableResource(R.color.background)

        ActivityHolder.setActivity(this)
        navigator.init(this, R.id.containerView)
        if (savedInstanceState == null) StartFlow.start()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Handler().postDelayed({
            deepLinkResolver.resolveIntent(intent)
        }, 2000)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

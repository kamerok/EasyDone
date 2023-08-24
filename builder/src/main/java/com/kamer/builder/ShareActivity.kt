package com.kamer.builder

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import org.koin.android.ext.android.inject
import easydone.coreui.design.R as designR


class ShareActivity : AppCompatActivity(R.layout.activity_container) {

    private val navigator: ActivityNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = CustomFragmentFactory

        hideSystemUI()

        super.onCreate(savedInstanceState)

        ActivityHolder.setActivity(this)
        navigator.init(this, R.id.containerView)
        if (savedInstanceState == null) StartFlow.startCreate(
            sharedText = intent.getStringExtra(Intent.EXTRA_TEXT).orEmpty()
        )
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, findViewById(android.R.id.content)).let { controller ->
            val isLightSystemBars = resources.getBoolean(designR.bool.light_system_bars)
            controller.isAppearanceLightStatusBars = isLightSystemBars
            controller.isAppearanceLightNavigationBars = isLightSystemBars
        }
    }

}

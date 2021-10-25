package com.kamer.builder

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity(R.layout.activity_container) {

    private val navigator: ActivityNavigator by inject()
    private val deepLinkResolver: DeepLinkResolver by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = CustomFragmentFactory

        setupSplashScreen()
        hideSystemUI()

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

    private fun setupSplashScreen() {
        installSplashScreen().setOnExitAnimationListener { splashScreenView ->
            ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)
                .apply {
                    duration = 500L
                    doOnEnd { splashScreenView.remove() }
                }
                .start()
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, findViewById(android.R.id.content)).let { controller ->
            val isLightSystemBars = resources.getBoolean(R.bool.light_system_bars)
            controller.isAppearanceLightStatusBars = isLightSystemBars
            controller.isAppearanceLightNavigationBars = isLightSystemBars
        }
    }
}

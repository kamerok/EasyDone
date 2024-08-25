package com.kamer.builder

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity(R.layout.activity_container) {

    private val navigator: ActivityNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        setupSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        ActivityHolder.setActivity(this)
        navigator.init(this, R.id.containerView)
        if (savedInstanceState == null) {
            if (intent.action == ACTION_SANDBOX) StartFlow.enableSandbox()
        }

        val mainNavigationScreenHolder = MainNavigationScreenHolder(this)
        setContent {
            mainNavigationScreenHolder.MainScreen()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
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

    companion object {
        private const val ACTION_SANDBOX = "easydone.action.SANDBOX"
    }
}

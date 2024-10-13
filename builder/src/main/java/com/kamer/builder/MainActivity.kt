package com.kamer.builder

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import easydone.coreui.design.AppTheme


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setupSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            if (intent.action == ACTION_SANDBOX) StartFlow.enableSandbox()
        }

        val mainNavigationScreenHolder = MainNavigationScreenHolder(this)
        setContent {
            AppTheme {
                // this is needed to update system colors when compose handles configuration changes without activity recreation
                LaunchedEffect(LocalConfiguration.current) {
                    enableEdgeToEdge()
                }
                mainNavigationScreenHolder.MainScreen()
            }
        }
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

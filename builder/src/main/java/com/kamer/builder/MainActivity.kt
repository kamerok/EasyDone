package com.kamer.builder

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import easydone.feature.inbox.InboxFragment
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity(R.layout.activity_container) {

    private val navigator: ActivityNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = CustomFragmentFactory

        setupSplashScreen()
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        ActivityHolder.setActivity(this)
        navigator.init(this, R.id.containerView)
        if (savedInstanceState == null) StartFlow.start(
            intent.action == ACTION_SANDBOX,
            !intent.isOpenedFromHistory() && intent.getBooleanExtra("inbox", false)
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        //check inbox deeplink
        if (intent.getBooleanExtra("inbox", false)) {
            navigator.clearStack()
            navigator.openScreen(InboxFragment::class.java, true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun Intent.isOpenedFromHistory(): Boolean =
        flags == (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY)

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

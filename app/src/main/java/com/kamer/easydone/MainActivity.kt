package com.kamer.easydone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.kamer.setupflow.SetupFragment
import com.kamer.setupflow.SetupStatusChecker

class MainActivity : AppCompatActivity() {

    private val setupStatusChecker = SetupStatusChecker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (setupStatusChecker.isSetupCompleted()) {
            startMainFlow()
        } else {
            startSetupFlow()
        }
    }

    private fun startSetupFlow() {
        supportFragmentManager.commit {
            replace(R.id.containerView, SetupFragment.create(SetupFragment.Dependencies(
                finishSetupListener = { startMainFlow() }
            )))
        }
    }

    private fun startMainFlow() {
        supportFragmentManager.commit {
            replace(R.id.containerView, MainFragment())
        }
    }
}

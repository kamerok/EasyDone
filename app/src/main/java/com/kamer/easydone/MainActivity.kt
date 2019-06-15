package com.kamer.easydone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.kamer.setupflow.SetupFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startSetupFlow()
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

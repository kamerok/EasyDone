package com.kamer.builder

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.kamer.home.HomeFragment
import com.kamer.setupflow.SetupFragment
import com.kamer.setupflow.SetupStatusChecker


object StartFlow {

    fun start(activity: AppCompatActivity, containerId: Int) {
        if (SetupStatusChecker().isSetupCompleted()) {
            startMainFlow(activity, containerId)
        } else {
            startSetupFlow(activity, containerId)
        }
    }

    private fun startSetupFlow(activity: AppCompatActivity, containerId: Int) {
        activity.supportFragmentManager.commit {
            replace(containerId, SetupFragment.create(SetupFragment.Dependencies(
                finishSetupListener = { startMainFlow(activity, containerId) }
            )))
        }
    }

    private fun startMainFlow(activity: AppCompatActivity, containerId: Int) {
        activity.supportFragmentManager.commit {
            replace(containerId, HomeFragment())
        }
    }

}
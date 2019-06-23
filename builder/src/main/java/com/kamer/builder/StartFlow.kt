package com.kamer.builder

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.kamer.home.HomeFragment
import com.kamer.login.LoginFragment
import com.kamer.selectboard.SelectBoardFragment
import com.kamer.setupflow.R
import com.kamer.setupflow.SetupFlowNavigator
import com.kamer.setupflow.SetupFragment
import easydone.core.auth.AuthInfoHolder


object StartFlow {

    private lateinit var authInfoHolder: AuthInfoHolder

    fun start(activity: AppCompatActivity, containerId: Int) {
//        authInfoHolder = AuthInfoHolder()

        if (authInfoHolder.getToken() != null && authInfoHolder.getBoardId() != null) {
            startMainFlow(activity, containerId)
        } else {
            startSetupFlow(activity, containerId)
        }
    }

    private fun startSetupFlow(activity: AppCompatActivity, containerId: Int) {
        activity.supportFragmentManager.commit {
            var fragment: Fragment? = null
            fragment = SetupFragment.create(SetupFragment.Dependencies(
                finishSetupListener = { startMainFlow(activity, containerId) },
                navigator = object : SetupFlowNavigator {
                    override fun navigateToLogin(loginListener: (String, String) -> Unit) {
                        fragment?.run { startLogin(this, loginListener) }
                    }

                    override fun navigateToSelectBoard(token: String, userId: String, listener: (String) -> Unit) {
                        fragment?.run { startSelectBoard(this, token, userId, listener) }
                    }
                },
                authInfoHolder = authInfoHolder
            ))
            replace(containerId, fragment)
        }
    }

    private fun startMainFlow(activity: AppCompatActivity, containerId: Int) {
        activity.supportFragmentManager.commit {
            replace(containerId, HomeFragment())
        }
    }

    private fun startLogin(fragment: Fragment, loginListener: (String, String) -> Unit) {
        fragment.childFragmentManager.commit {
            replace(R.id.container, LoginFragment.create(LoginFragment.Dependencies(loginListener)))
        }
    }

    private fun startSelectBoard(fragment: Fragment, token: String, userId: String, listener: (String) -> Unit) {
        fragment.childFragmentManager.commit {
            replace(
                R.id.container, SelectBoardFragment.create(
                    SelectBoardFragment.Dependencies(
                        token = token,
                        userId = userId,
                        listener = listener
                    )
                ))
        }
    }

}
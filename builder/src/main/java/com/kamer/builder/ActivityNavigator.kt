package com.kamer.builder

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import easydone.library.navigation.Navigator
import easydone.library.navigation.FragmentManagerNavigator


class ActivityNavigator : Navigator {

    private var navigator: Navigator? = null

    override fun openScreen(fragment: Fragment, addToBackStack: Boolean) {
        navigator?.openScreen(fragment, addToBackStack)
    }

    override fun isEmpty(): Boolean = navigator?.isEmpty() ?: true

    override fun popScreen() {
        navigator?.popScreen()
    }

    override fun clearStack() {
        navigator?.clearStack()
    }

    fun init(activity: AppCompatActivity, containerId: Int) {
        navigator = FragmentManagerNavigator(activity.supportFragmentManager, containerId)
        activity.onBackPressedDispatcher.addCallback(
            activity,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!isEmpty()) {
                        navigator?.popScreen()
                    } else {
                        isEnabled = false
                        activity.onBackPressed()
                        isEnabled = true
                    }
                }
            }
        )
    }
}

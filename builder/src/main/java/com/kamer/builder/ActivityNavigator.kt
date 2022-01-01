package com.kamer.builder

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import easydone.library.navigation.FragmentManagerNavigator
import easydone.library.navigation.Navigator


class ActivityNavigator : Navigator {

    private var navigator: Navigator? = null

    override fun openScreen(fragment: Fragment, addToBackStack: Boolean) {
        navigator?.openScreen(fragment, addToBackStack)
    }

    override fun openScreen(
        fragmentClass: Class<out Fragment>,
        addToBackStack: Boolean,
        args: Bundle?
    ) {
        navigator?.openScreen(fragmentClass, addToBackStack, args)
    }

    override fun setupScreenStack(vararg fragmentClasses: Class<out Fragment>) {
        navigator?.setupScreenStack(*fragmentClasses)
    }

    override fun isEmpty(): Boolean = navigator?.isEmpty() ?: true

    override fun popScreen() {
        navigator?.popScreen()
    }

    override fun clearStack() {
        navigator?.clearStack()
    }

    fun init(activity: AppCompatActivity, containerId: Int) {
        val activityNavigator =
            FragmentManagerNavigator(activity.supportFragmentManager, containerId)
        navigator = activityNavigator
        activity.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_START -> navigator = activityNavigator
                    Lifecycle.Event.ON_STOP -> navigator = null
                }
            }
        })
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

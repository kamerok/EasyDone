package com.kamer.builder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit


class Navigator(private val fragmentManager: FragmentManager, private val containerId: Int) {

    fun openScreen(fragment: Fragment, addToBackStack: Boolean = false) =
        fragmentManager.commit {
            setCustomAnimations(
                R.animator.fragment_open_enter, R.animator.fragment_open_exit,
                R.animator.fragment_close_enter, R.animator.fragment_close_exit
            )
            replace(containerId, fragment)
            if (addToBackStack) addToBackStack(null)
        }

    fun isEmpty(): Boolean = fragmentManager.backStackEntryCount == 0

    fun popScreen() {
        fragmentManager.popBackStack()
    }

    fun clearStack() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

}
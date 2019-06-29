package com.kamer.builder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit


class Navigator(private val fragmentManager: FragmentManager, private val containerId: Int) {

    fun openScreen(fragment: Fragment, addToBackStack: Boolean = false) =
        fragmentManager.commit {
            replace(containerId, fragment)
            if (addToBackStack) addToBackStack(null)
        }

}
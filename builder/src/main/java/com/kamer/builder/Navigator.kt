package com.kamer.builder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commitNow


class Navigator(private val fragmentManager: FragmentManager, private val containerId: Int) {

    fun openScreen(fragment: Fragment) = fragmentManager.commitNow { replace(containerId, fragment) }

}
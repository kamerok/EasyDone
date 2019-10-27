package com.kamer.builder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import easydone.feature.feed.FeedFragment
import easydone.feature.home.HomeFragment
import org.koin.core.context.GlobalContext


class CustomFragmentFactory : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
        when (className) {
            HomeFragment::class.java.name -> GlobalContext.get().koin.get<HomeFragment>()
            FeedFragment::class.java.name -> GlobalContext.get().koin.get<FeedFragment>()
            else -> super.instantiate(classLoader, className)
        }

}

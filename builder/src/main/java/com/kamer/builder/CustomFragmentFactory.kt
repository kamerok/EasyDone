package com.kamer.builder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import easydone.feature.edittask.EditTaskFragment
import easydone.feature.home.HomeFragment
import easydone.feature.inbox.InboxFragment
import easydone.feature.quickcreatetask.QuickCreateTaskFragment
import easydone.feature.settings.SettingsFragment
import easydone.feature.setupflow.SetupFragment
import easydone.feature.taskdetails.TaskDetailsFragment
import easydone.feature.waiting.WaitingFragment
import org.koin.core.context.GlobalContext


object CustomFragmentFactory : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
        when (className) {
            HomeFragment::class.java.name -> GlobalContext.get().get<HomeFragment>()
            InboxFragment::class.java.name -> GlobalContext.get().get<InboxFragment>()
            WaitingFragment::class.java.name -> GlobalContext.get().get<WaitingFragment>()
            TaskDetailsFragment::class.java.name -> GlobalContext.get().get<TaskDetailsFragment>()
            EditTaskFragment::class.java.name -> GlobalContext.get().get<EditTaskFragment>()
            QuickCreateTaskFragment::class.java.name -> GlobalContext.get()
                .get<QuickCreateTaskFragment>()
            SettingsFragment::class.java.name -> GlobalContext.get().get<SettingsFragment>()
            SetupFragment::class.java.name -> GlobalContext.get().get<SetupFragment>()
            else -> super.instantiate(classLoader, className)
        }

}

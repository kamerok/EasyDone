package com.kamer.builder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import easydone.core.domain.DomainRepository
import easydone.core.domain.RemoteDataSource
import easydone.core.domain.SyncScheduler
import easydone.core.domain.model.Task
import easydone.feature.edittask.EditTaskArgs
import easydone.feature.edittask.EditTaskNavigator
import easydone.feature.edittask.EditTaskRoute
import easydone.feature.home.HomeNavigator
import easydone.feature.home.HomeRoute
import easydone.feature.inbox.InboxNavigator
import easydone.feature.inbox.InboxRoute
import easydone.feature.settings.SettingScreen
import easydone.feature.settings.SettingsNavigator
import easydone.feature.setupflow.SetupRoute
import easydone.feature.taskdetails.TaskDetailsNavigator
import easydone.feature.taskdetails.TaskDetailsRoute
import easydone.feature.waiting.WaitingNavigator
import easydone.feature.waiting.WaitingRoute
import easydone.service.trello.TrelloRemoteDataSource
import easydone.service.trello.api.TrelloApi
import kotlinx.coroutines.runBlocking

class MainNavigationFragment(
    private val syncScheduler: SyncScheduler,
    private val domainRepository: DomainRepository,
    private val remoteDataSource: RemoteDataSource,
    private val settingsNavigator: SettingsNavigator,
    private val trelloRemoteDataSource: TrelloRemoteDataSource,
    private val trelloApi: TrelloApi,
    private val trelloApiKey: String,
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {

        fun NavController.openViewTask(id: String) {
            navigate("task/$id")
        }

        fun NavController.openEditTask(id: String) {
            navigate("task/edit/$id")
        }

        fun NavController.openCreateTask() {
            navigate("task/create")
        }

        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            val navController = rememberNavController()
            val isRemoteDataSourceConnected = remember {
                runBlocking { remoteDataSource.isConnected() }
            }
            val startDestination = if (isRemoteDataSourceConnected) {
                "home"
            } else {
                "setup"
            }

            LaunchedEffect(Unit) {
                val intent = requireActivity().intent
                val isInboxLaunch = !intent.isOpenedFromHistory() &&
                        intent.getBooleanExtra("inbox", false)
                if (isInboxLaunch) {
                    navController.navigate("inbox")
                }
            }

            DisposableEffect(Unit) {
                val listener = Consumer<Intent> { intent ->
                    //check inbox deeplink
                    if (intent.getBooleanExtra("inbox", false)) {
                        with(navController) {
                            navigate("inbox", navOptions {
                                popUpTo("home") {}
                            })
                        }
                    }
                }
                requireActivity().addOnNewIntentListener(listener)
                onDispose { requireActivity().removeOnNewIntentListener(listener) }
            }

            NavHost(navController = navController, startDestination = startDestination) {
                composable("setup") {
                    SetupRoute(
                        trelloRemoteDataSource = trelloRemoteDataSource,
                        trelloApi = trelloApi,
                        trelloApiKey = trelloApiKey
                    ) {
                        navController.popBackStack("setup", true)
                        navController.navigate("home")
                    }
                }

                composable("home") {
                    HomeRoute(syncScheduler, domainRepository, object : HomeNavigator {
                        override fun navigateToCreate() {
                            navController.openCreateTask()
                        }

                        override fun navigateToSettings() {
                            navController.navigate("settings")
                        }

                        override fun navigateToInbox() {
                            navController.navigate("inbox")
                        }

                        override fun navigateToWaiting() {
                            navController.navigate("waiting")
                        }

                        override fun navigateToTask(id: String) {
                            navController.openViewTask(id)
                        }
                    })
                }

                composable("inbox") {
                    InboxRoute(
                        domainRepository,
                        object : InboxNavigator {
                            override fun openTask(id: String) {
                                navController.openViewTask(id)
                            }

                            override fun close() {
                                navController.popBackStack()
                            }
                        }
                    )
                }

                composable("waiting") {
                    WaitingRoute(domainRepository, object : WaitingNavigator {
                        override fun openTask(id: String) {
                            navController.openViewTask(id)
                        }
                    })
                }

                composable("settings") {
                    SettingScreen(remoteDataSource, settingsNavigator)
                }

                composable(
                    route = "task/{taskId}",
                    arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val id = checkNotNull(backStackEntry.arguments?.getString("taskId")) {
                        "Task id is required"
                    }
                    TaskDetailsRoute(id, domainRepository, object : TaskDetailsNavigator {
                        override fun editTask(id: String) {
                            navController.openEditTask(id)
                        }

                        override fun close() {
                            navController.popBackStack()
                        }
                    })
                }

                composable(
                    route = "task/edit/{taskId}",
                    arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val id = checkNotNull(backStackEntry.arguments?.getString("taskId")) {
                        "Task id is required"
                    }
                    val args = remember { EditTaskArgs.Edit(id) }
                    EditTaskRoute(args, domainRepository, object : EditTaskNavigator {
                        override fun close() {
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    })
                }

                composable("task/create") {
                    EditTaskRoute(
                        EditTaskArgs.Create(type = Task.Type.ToDo),
                        domainRepository,
                        object : EditTaskNavigator {
                            override fun close() {
                                requireActivity().onBackPressedDispatcher.onBackPressed()
                            }
                        })
                }
            }
        }
    }

    private fun Intent.isOpenedFromHistory(): Boolean =
        flags == (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY)

}
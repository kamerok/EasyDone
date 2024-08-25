package com.kamer.builder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import easydone.core.domain.DomainRepository
import easydone.core.domain.RemoteDataSource
import easydone.core.domain.SyncScheduler
import easydone.feature.home.HomeNavigator
import easydone.feature.home.HomeRoute
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
    private val homeNavigator: HomeNavigator,
    private val taskDetailsNavigator: TaskDetailsNavigator,
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
                        override fun navigateToCreate() = homeNavigator.navigateToCreate()

                        override fun navigateToSettings() {
                            navController.navigate("settings")
                        }

                        override fun navigateToInbox() = homeNavigator.navigateToInbox()

                        override fun navigateToWaiting() {
                            navController.navigate("waiting")
                        }

                        override fun navigateToTask(id: String) {
                            navController.openViewTask(id)
                        }
                    })
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
                            taskDetailsNavigator.editTask(id)
                        }

                        override fun close() {
                            navController.popBackStack()
                        }
                    })
                }
            }
        }
    }

}
package com.skilltracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.skilltracker.domain.repository.SkillRepository
import com.skilltracker.ui.addedit.AddEditSkillScreen
import com.skilltracker.ui.detail.DetailScreen
import com.skilltracker.ui.home.HomeScreen
import com.skilltracker.viewmodel.AddEditViewModel
import com.skilltracker.viewmodel.DetailViewModel
import com.skilltracker.viewmodel.HomeViewModel

@Composable
fun SkillTrackerNavGraph(
    repository: SkillRepository,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(repository)
            )
            HomeScreen(
                viewModel = viewModel,
                onNavigateToAdd = { navController.navigate("add") },
                onNavigateToDetail = { skillId ->
                    navController.navigate("detail/$skillId")
                }
            )
        }

        composable(
            route = "detail/{skillId}",
            arguments = listOf(navArgument("skillId") { type = NavType.StringType })
        ) { backStackEntry ->
            val skillId = backStackEntry.arguments?.getString("skillId") ?: return@composable
            val viewModel: DetailViewModel = viewModel(
                factory = DetailViewModel.Factory(repository, skillId)
            )
            DetailScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate("edit/$skillId") }
            )
        }

        composable("add") {
            val viewModel: AddEditViewModel = viewModel(
                factory = AddEditViewModel.Factory(repository)
            )
            AddEditSkillScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "edit/{skillId}",
            arguments = listOf(navArgument("skillId") { type = NavType.StringType })
        ) { backStackEntry ->
            val skillId = backStackEntry.arguments?.getString("skillId") ?: return@composable
            val viewModel: AddEditViewModel = viewModel(
                factory = AddEditViewModel.Factory(repository, skillId)
            )
            AddEditSkillScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

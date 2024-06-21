package com.example.insight.presentation.ui.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.insight.presentation.ui.screens.assign_preferred_app.AssignPreferredAppScreen
import com.example.insight.presentation.ui.screens.choose_gesture.ChooseGestureScreen
import com.example.insight.presentation.ui.screens.drawing.DrawingScreen
import com.example.insight.presentation.ui.screens.environment_sensing.EnvironmentSensingScreen
import com.example.insight.presentation.ui.screens.tutorial.TutorialScreen

enum class InsightScreens {
    Tutorial,
    Drawing,
    EnvironmentSensing,
    ChooseGesture,
    AssignPreferredApp
}

@Composable
fun InsightApp(
    viewModel: PreferencesViewModel = viewModel(
        factory = PreferencesViewModel.Factory
    )
) {
    val navController: NavHostController = rememberNavController()

    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        modifier = Modifier
            .fillMaxSize(),
        navController = navController,
        startDestination = InsightScreens.Tutorial.name
    ) {
        composable(
            route = InsightScreens.Tutorial.name
        ) {
            TutorialScreen(
                modifier = Modifier.fillMaxSize(),
                navigateToDrawingScreen = {
                    navController.navigate(InsightScreens.Drawing.name)
                }
            )
        }
        composable(
            route = InsightScreens.Drawing.name
        ) {
            DrawingScreen(
                modifier = Modifier
                    .fillMaxSize(),
                preferredApps = uiState.preferredApps,
                navigateToEnvironmentSensingScreen = {
                    navController.navigate(InsightScreens.EnvironmentSensing.name)
                },
                navigateToChooseGestureScreen = {
                    navController.navigate(InsightScreens.ChooseGesture.name)
                },
                navigateToAssignPreferredAppScreen = { indexOfGesture ->
                    navController.navigate(
                        InsightScreens.AssignPreferredApp.name + indexOfGesture.toString()
                    )
                }
            )
        }
        composable(
            route = InsightScreens.EnvironmentSensing.name
        ) {
            EnvironmentSensingScreen()
        }
        composable(
            route = InsightScreens.ChooseGesture.name
        ) {
            ChooseGestureScreen(
                modifier = Modifier
                    .fillMaxSize(),
                navigateToDrawingScreen = {
                    navController.popBackStack(
                        route = InsightScreens.Drawing.name,
                        inclusive = false
                    )
                },
                navigateToAssignPreferredAppScreen = { indexOfGesture ->
                    navController.navigate(
                        InsightScreens.AssignPreferredApp.name + "/$indexOfGesture"
                    )
                }
            )
        }
        composable(
            route = InsightScreens.AssignPreferredApp.name + "/{index_of_gesture}",
            arguments = listOf(
                navArgument("index_of_gesture") {
                    type = NavType.IntType
                }
            )
        ) {
            AssignPreferredAppScreen(
                getInstalledApps = viewModel::getInstalledApps,
                setPreferredApp = { preferredApp: App ->
                    viewModel.setPreferredApp(
                        indexOfGesture = it.arguments!!.getInt("index_of_gesture"),
                        preferredApp = preferredApp
                    )
                },
                navigateToDrawingScreen = {
                    navController.popBackStack(
                        route = InsightScreens.Drawing.name,
                        inclusive = false
                    )
                }
            )
        }
    }
}
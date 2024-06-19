package com.example.insight.presentation.ui.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
    preferencesViewModel: PreferencesViewModel = viewModel(
        factory = PreferencesViewModel.Factory
    )
) {
    val navController: NavHostController = rememberNavController()

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
                navigateToAssignPreferredAppScreen = { indexOfGesture ->
                    navController.navigate(
                        InsightScreens.AssignPreferredApp.name + indexOfGesture.toString()
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
                indexOfGesture = it.arguments!!.getInt("index_of_gesture")
            )
        }
    }
}
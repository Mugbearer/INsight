package com.example.insight

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.insight.state.AppViewModel
import com.example.insight.state.helperfunctions.useTts
import com.example.insight.ui.AssignAppToGesture
import com.example.insight.ui.EnvironmentSensingScreen
import com.example.insight.ui.PreferredAppScreen
import com.example.insight.ui.StartScreen
import com.example.insight.ui.TutorialScreen
import com.example.insight.ui.theme.INsightTheme

enum class INsightScreen() {
    Tutorial,
    Start,
    EnvironmentSensing,
    AssignPreferredApp,
    AssignAppToGesture
}

@Composable
fun InsightApp(
    viewModel: AppViewModel = viewModel(
        factory = AppViewModel.Factory
    ),
) {
    val navController: NavHostController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        modifier = Modifier
            .fillMaxSize(),
        navController = navController,
        startDestination = INsightScreen.Tutorial.name
    ) {
        composable(route = INsightScreen.Tutorial.name) {
            TutorialScreen(
                modifier = Modifier.fillMaxSize(),
                navigateToStartScreen = {
                    it.useTts("Please perform gesture")
                    navController.navigate(INsightScreen.Start.name)
                }
            )
        }
        composable(route = INsightScreen.Start.name) {
            StartScreen(
                modifier = Modifier
                    .fillMaxSize(),
                isDrawing = uiState.isDrawing,
                setIsDrawing = {
                    viewModel.setIsDrawing(it)
                },
                addLine = { change, dragAmount ->
                    viewModel.addLine(change, dragAmount)
                },
                lines = uiState.lines,
                detectGesture = { context, canvasSize ->
                    viewModel.detectGesture(
                        context,
                        canvasSize
                    )
                },
                setEnvironmentSensingBitmap = viewModel::setEnvironmentSensingBitmap,
                navigateToEnvironmentSensing = {
                    navController.navigate(INsightScreen.EnvironmentSensing.name)
                },
                preferredApps = uiState.preferredApps,
                navigateToAssignAppToGesture = {
                    navController.navigate(INsightScreen.AssignAppToGesture.name)
                },
                navigateToPreferredApp = { context, indexOfGesture ->
                    context.useTts(
                        "Please choose an app. Tap to navigate next, double tap to navigate back. Long press to select app"
                    )
                    viewModel.getMapOfApps(context = context)
                    navController.navigate(
                        INsightScreen.AssignPreferredApp.name + "/$indexOfGesture"
                    )
                }
            )
        }
        composable(route = INsightScreen.EnvironmentSensing.name) {
            EnvironmentSensingScreen(
                modifier = Modifier.fillMaxSize(),
                senseEnvironment = {
                    viewModel.senseEnvironment(it)
                },
                environmentResults = uiState.environmentResults,
                setEnvironmentResults = {
                    viewModel.setEnvironmentResults(it)
                },
                navigateToStartScreen = {
                    navController.popBackStack(INsightScreen.Start.name, inclusive = false)
                }
            )
        }
        composable(route = INsightScreen.AssignAppToGesture.name) {
            AssignAppToGesture(
                modifier = Modifier.fillMaxSize(),
                isDrawing = uiState.isDrawing,
                setIsDrawing = viewModel::setIsDrawing,
                addLine = viewModel::addLine,
                lines = uiState.lines,
                detectGesture = viewModel::detectGesture,
                navigateToAssignPreferredApp = { context, indexOfGesture ->
                    context.useTts(
                        "Please choose an app. Tap to navigate next, double tap to navigate back. Long press to select app"
                    )
                    viewModel.getMapOfApps(context = context)
                    navController.navigate(
                        INsightScreen.AssignPreferredApp.name + "/$indexOfGesture"
                    )
                },
                navigateToStartScreen = {
                    navController.popBackStack(INsightScreen.Start.name, inclusive = false)
                }
            )
        }
        composable(
            route = INsightScreen.AssignPreferredApp.name + "/{index_of_gesture}",
            arguments = listOf(
                navArgument("index_of_gesture") {
                    type = NavType.IntType
                }
            )
        ) {navBackStackEntry ->
            val indexOfGesture = navBackStackEntry.arguments?.getInt("index_of_gesture")!!
            PreferredAppScreen(
                modifier = Modifier.fillMaxSize(),
                indexOfGesture = indexOfGesture,
                listOfInstalledApps = uiState.listOfInstalledApps,
                selectedApp = uiState.selectedApp,
                getSelectedApp = viewModel::getSelectedApp,
                navigateToNextButtonAndReturnAppName = viewModel::navigateToNextButtonAndReturnAppName,
                navigateToPreviousButtonAndReturnAppName = viewModel::navigateToPreviousButtonAndReturnAppName,
                setPreferredApp = {
                    viewModel.setPreferredApp(it)
                    navController.popBackStack(INsightScreen.Start.name, inclusive = false)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InsightAppPreview() {
    INsightTheme {
        InsightApp()
    }
}
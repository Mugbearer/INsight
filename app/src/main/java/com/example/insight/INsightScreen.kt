package com.example.insight

import androidx.annotation.StringRes
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
import com.example.insight.ui.AssignAppToGesture
import com.example.insight.ui.EnvironmentSensingScreen
import com.example.insight.ui.PreferredAppScreen
import com.example.insight.ui.StartScreen
import com.example.insight.ui.theme.INsightTheme

enum class INsightScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    EnvironmentSensing(title = R.string.environment_sensing),
    AssignPreferredApp(title = R.string.assign_preferred_app),
    AssignAppToGesture(title = R.string.assign_app_to_gesture)
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
        startDestination = INsightScreen.Start.name
    ) {
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
                environmentSensingBitmap = uiState.environmentSensingBitmap!!,
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
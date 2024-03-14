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
import com.example.insight.ui.AssignNewGesture
import com.example.insight.ui.AssignOldGesture
import com.example.insight.ui.EnvironmentSensingScreen
import com.example.insight.ui.PreferredAppScreen
import com.example.insight.ui.StartScreen
import com.example.insight.ui.theme.INsightTheme

enum class INsightScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    EnvironmentSensing(title = R.string.environment_sensing),
    AssignPreferredApp(title = R.string.assign_preferred_app),
    AssignOldGesture(title = R.string.assign_old_gesture),
    AssignNewGesture(title = R.string.assign_new_gesture)
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
                setEnvironmentSensingBitmap = {
                    viewModel.setEnvironmentSensingBitmap(it)
                },
                navigateToEnvironmentSensing = {
                    navController.navigate(INsightScreen.EnvironmentSensing.name)
                },
                preferredApp = uiState.preferredApp,
                navigateToPreferredApp = {
                    navController.navigate(INsightScreen.AssignPreferredApp.name)
                },
                navigateToAssignOldGesture = {
                    navController.navigate(INsightScreen.AssignOldGesture.name)
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
        composable(route = INsightScreen.AssignPreferredApp.name) {
            PreferredAppScreen(
                modifier = Modifier.fillMaxSize(),
                getHashMapOfApps = viewModel::getMapOfApps,
                setPreferredApp = {
                    viewModel.setPreferredApp(it)
                    navController.popBackStack(INsightScreen.Start.name, inclusive = false)
                }
            )
        }
        composable(route = INsightScreen.AssignOldGesture.name) {
            AssignOldGesture(
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
                navigateToAssignNewGesture = {
                    navController.navigate(
                        INsightScreen.AssignNewGesture.name + "/$it"
                    )
                }
            )
        }
        composable(
            route = INsightScreen.AssignNewGesture.name + "/{old_gesture}",
            arguments = listOf(
                navArgument("old_gesture") {
                    type = NavType.IntType
                }
            )
        ) { navBackStackEntry ->
            val oldGesture = navBackStackEntry.arguments?.getInt("old_gesture")!!
            AssignNewGesture(
                modifier = Modifier.fillMaxSize(),
                oldGesture = oldGesture,
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
                swapGestures = viewModel::swapGestures,
                navigateToStartScreen = {
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
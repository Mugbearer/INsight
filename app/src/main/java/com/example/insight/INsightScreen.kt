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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.insight.state.AppViewModel
import com.example.insight.ui.EnvironmentSensingScreen
import com.example.insight.ui.StartScreen
import com.example.insight.ui.theme.INsightTheme

enum class INsightScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    EnvironmentSensing(title = R.string.environment_sensing),
    Settings(title = R.string.settings),
    Gestures(title = R.string.assign_gestures),
    MainContact(title = R.string.assign_main_contact)
}

@Composable
fun InsightApp(
    viewModel: AppViewModel = viewModel(),
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
                modifier = Modifier.fillMaxSize(),
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
                    navController.navigate(INsightScreen.Start.name)
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
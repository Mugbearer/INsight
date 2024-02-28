package com.example.insight

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.insight.state.GestureViewModel
import com.example.insight.ui.StartScreen
import com.example.insight.ui.theme.INsightTheme

enum class INsightScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Settings(title = R.string.settings),
    Gestures(title = R.string.assign_gestures),
    MainContact(title = R.string.assign_main_contact)
}

@Composable
fun InsightApp(
    viewModel: GestureViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = rememberNavController(),
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
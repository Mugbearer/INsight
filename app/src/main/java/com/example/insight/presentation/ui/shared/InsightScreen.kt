package com.example.insight.presentation.ui.shared

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.insight.R
import com.example.insight.presentation.ui.screens.assign_preferred_app.AssignPreferredAppScreen
import com.example.insight.presentation.ui.screens.choose_gesture.ChooseGestureScreen
import com.example.insight.presentation.ui.screens.drawing.DrawingScreen
import com.example.insight.presentation.ui.screens.environment_sensing.EnvironmentSensingScreen
import com.example.insight.presentation.ui.screens.tutorial.TutorialScreen
import kotlinx.coroutines.Deferred

enum class InsightScreens {
    Tutorial,
    Drawing,
    EnvironmentSensing,
    ChooseGesture,
    AssignPreferredApp
}

@Composable
fun InsightApp(
    viewModel: InsightViewModel = viewModel(
        factory = InsightViewModel.Factory
    )
) {
    val activityContext = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()

    val instructions: String =  stringResource(id = R.string.tutorial)

    LaunchedEffect(Unit) {
        viewModel.initTextToSpeech(instructions)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopTextToSpeech()
            viewModel.shutdownTextToSpeech()
        }
    }

    InsightContent(
        preferredApps = uiState.preferredApps,
        getInstalledApps = viewModel::getInstalledApps,
        setPreferredApp = viewModel::setPreferredApp,
        getBitmapFromCanvas = viewModel::getBitmapFromCanvas,
        getIndexOfResult = viewModel::getIndexOfResult,
        isExistsApp = viewModel::isExistsApp,
        speakTextToSpeech = viewModel::speakTextToSpeech,
        stopTextToSpeech = viewModel::stopTextToSpeech,
        getRingerMode = viewModel::getRingerMode,
        setRingerMode = viewModel::setRingerMode,
        redirectToGoogle = {
            viewModel.redirectToGoogle(activityContext)
        },
        redirectToSettings = {
            viewModel.redirectToSettings(activityContext)
        },
        redirectToKeypad = {
            viewModel.redirectToKeypad(activityContext)
        },
        redirectToPreferredApp = { index ->
            viewModel.redirectToPreferredApp(activityContext, index)
        },
        redirectToContacts = {
            viewModel.redirectToContacts(activityContext)
        }
    )
}

@Composable
fun InsightContent(
    preferredApps: List<App>,
    getInstalledApps: () -> Deferred<List<App>>,
    setPreferredApp: (Int, App) -> Unit,
    getBitmapFromCanvas: (Size, List<Line>) -> Deferred<Bitmap>,
    getIndexOfResult: (Bitmap) -> Deferred<Int>,
    isExistsApp: (Int) -> Boolean,
    speakTextToSpeech: (String) -> Unit,
    stopTextToSpeech: () -> Unit,
    getRingerMode: () -> Int,
    setRingerMode: (Int) -> Unit,
    redirectToGoogle: () -> Unit,
    redirectToSettings: () -> Unit,
    redirectToKeypad: () -> Unit,
    redirectToPreferredApp: (Int) -> Unit,
    redirectToContacts: () -> Unit,
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
                speakTextToSpeech = speakTextToSpeech,
                stopTextToSpeech = stopTextToSpeech,
                navigateToDrawingScreen = {
                    navController.navigate(InsightScreens.Drawing.name)
                }
            )
        }
        composable(
            route = InsightScreens.Drawing.name
        ) {
            DrawingScreen(
                modifier = Modifier.fillMaxSize(),
                preferredApps = preferredApps,
                getBitmapFromCanvas = getBitmapFromCanvas,
                getIndexOfResult = getIndexOfResult,
                isExistsApp = isExistsApp,
                speakTextToSpeech = speakTextToSpeech,
                stopTextToSpeech = stopTextToSpeech,
                getRingerMode = getRingerMode,
                setRingerMode = setRingerMode,
                redirectToGoogle = redirectToGoogle,
                redirectToSettings = redirectToSettings,
                redirectToKeypad = redirectToKeypad,
                redirectToPreferredApp = redirectToPreferredApp,
                redirectToContacts = redirectToContacts,
                navigateToEnvironmentSensingScreen = {
//                    navController.navigate(InsightScreens.EnvironmentSensing.name)
                },
                navigateToChooseGestureScreen = {
                    navController.navigate(InsightScreens.ChooseGesture.name)
                },
                navigateToAssignPreferredAppScreen = { indexOfGesture ->
                    navController.navigate(
                        InsightScreens.AssignPreferredApp.name + "/$indexOfGesture"
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
                modifier = Modifier.fillMaxSize(),
                getBitmapFromCanvas = getBitmapFromCanvas,
                getIndexOfResult = getIndexOfResult,
                speakTextToSpeech = speakTextToSpeech,
                stopTextToSpeech = stopTextToSpeech,
                navigateToDrawingScreen = {
                    navController.popBackStack(
                        route = InsightScreens.Drawing.name,
                        inclusive = false
                    )
                    navController.navigate(InsightScreens.Drawing.name)
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
                modifier = Modifier.fillMaxSize(),
                indexOfGesture = it.arguments!!.getInt("index_of_gesture"),
                getInstalledApps = getInstalledApps,
                setPreferredApp = setPreferredApp,
                speakTextToSpeech = speakTextToSpeech,
                stopTextToSpeech = stopTextToSpeech,
                navigateToDrawingScreen = {
                    navController.popBackStack(
                        route = InsightScreens.Drawing.name,
                        inclusive = false
                    )
                    navController.navigate(InsightScreens.Drawing.name)
                }
            )
        }
    }
}
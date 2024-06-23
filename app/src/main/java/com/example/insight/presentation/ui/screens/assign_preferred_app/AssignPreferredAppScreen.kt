package com.example.insight.presentation.ui.screens.assign_preferred_app

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.insight.R
import com.example.insight.presentation.ui.shared.App
import com.example.insight.presentation.ui.shared.components.Loading
import kotlinx.coroutines.Deferred

@Composable
fun AssignPreferredAppScreen(
    modifier: Modifier = Modifier,
    viewModel: AssignPreferredAppViewModel = viewModel(),
    indexOfGesture: Int,
    getInstalledApps: () -> Deferred<List<App>>,
    setPreferredApp: (Int, App) -> Unit,
    speakTextToSpeech: (String) -> Unit,
    stopTextToSpeech: () -> Unit,
    navigateToDrawingScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AssignPreferredAppContent(
        modifier = modifier,
        uiState = uiState,
        indexOfGesture = indexOfGesture,
        setInstalledApps = viewModel::setInstalledApps,
        selectNextAppAndReturnAppName = viewModel::selectNextAppAndReturnAppName,
        selectPreviousAppAndReturnAppName = viewModel::selectPreviousAppAndReturnAppName,
        setIsLoading = viewModel::setIsLoading,
        getInstalledApps = getInstalledApps,
        setPreferredApp = setPreferredApp,
        speakTextToSpeech = speakTextToSpeech,
        stopTextToSpeech = stopTextToSpeech,
        navigateToDrawingScreen = navigateToDrawingScreen
    )
}

@Composable
fun AssignPreferredAppContent(
    modifier: Modifier = Modifier,
    uiState: AssignPreferredAppUiState,
    indexOfGesture: Int,
    setInstalledApps: (List<App>) -> Unit,
    selectNextAppAndReturnAppName: () -> String,
    selectPreviousAppAndReturnAppName: () -> String,
    setIsLoading: (Boolean) -> Unit,
    getInstalledApps: () -> Deferred<List<App>>,
    setPreferredApp: (Int, App) -> Unit,
    speakTextToSpeech: (String) -> Unit,
    stopTextToSpeech: () -> Unit,
    navigateToDrawingScreen: () -> Unit
) {
    val controlInstructions: String = stringResource(id = R.string.control_instructions)

    LaunchedEffect(Unit) {
        stopTextToSpeech()
        speakTextToSpeech("Currently fetching your list of apps, please wait.")

        setInstalledApps(
            getInstalledApps().await()
        )

        setIsLoading(false)
    }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            stopTextToSpeech()
            speakTextToSpeech(controlInstructions)
        }
    }

    if (uiState.isLoading) {
        Loading(
            modifier = modifier,
            "Fetching installed apps..."
        )
    } else {
        LazyColumn(
            modifier = modifier
                .pointerInput(uiState.indexOfSelectedApp) {
                    detectTapGestures(
                        onTap = {
                            stopTextToSpeech()
                            speakTextToSpeech(
                                selectNextAppAndReturnAppName()
                            )
                        },
                        onDoubleTap = {
                            stopTextToSpeech()
                            speakTextToSpeech(
                                selectPreviousAppAndReturnAppName()
                            )
                        },
                        onLongPress = {
                            stopTextToSpeech()
                            if (uiState.indexOfSelectedApp == null) {
                                speakTextToSpeech("Please select an app.")
                                return@detectTapGestures
                            }

                            val selectedApp: App =
                                uiState.installedApps[uiState.indexOfSelectedApp]

                            Log.d("indexOfSelectedApp",uiState.indexOfSelectedApp.toString())
                            Log.d("selectedApp.appName", selectedApp.appName)

                            setPreferredApp(indexOfGesture, selectedApp)

                            speakTextToSpeech(
                                "Successfully assigned ${selectedApp.appName} as your preferred app." +
                                        " Returning to the gesture drawing screen"
                            )

                            navigateToDrawingScreen()
                        }
                    )
                },
        ) {
            items(uiState.installedApps.size) { indexOfApp ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.installedApps[indexOfApp].appName,
                        style = TextStyle(fontSize = 16.sp),
                        color = if (
                            uiState.indexOfSelectedApp != null &&
                            uiState.indexOfSelectedApp == indexOfApp
                        ) {
                            Color.Green
                        } else Color.Black
                    )
                    HorizontalDivider(thickness = 1.dp, color = Color.Black)
                }
            }
        }
    }
}
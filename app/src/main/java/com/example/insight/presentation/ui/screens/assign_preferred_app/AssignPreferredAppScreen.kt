package com.example.insight.presentation.ui.screens.assign_preferred_app

import android.content.Context
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.insight.R
import com.example.insight.presentation.ui.shared.App
import com.example.insight.presentation.ui.shared.components.Loading
import kotlinx.coroutines.Deferred

@Composable
fun AssignPreferredAppScreen(
    modifier: Modifier = Modifier,
    viewModel: AssignPreferredAppViewModel = viewModel(),
    getInstalledApps: (Context) -> Deferred<List<App>>,
    setPreferredApp: (App) -> Unit,
    navigateToDrawingScreen: () -> Unit
) {
    val context: Context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()

    val controlInstructions: String = stringResource(id = R.string.control_instructions)

    LaunchedEffect(Unit) {
        viewModel.initTextToSpeech(context)

        viewModel.setInstalledApps(
            getInstalledApps(context).await()
        )
    }

    LaunchedEffect(uiState.installedApps) {
        viewModel.speakTextToSpeech(controlInstructions)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.shutdownTextToSpeech()
        }
    }

    if (uiState.installedApps.isEmpty()) {
        Loading()
    } else {
        LazyColumn(
            modifier = modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            viewModel.selectNextApp()
                            viewModel.speakSelectedApp()
                        },
                        onDoubleTap = {
                            viewModel.selectPreviousApp()
                            viewModel.speakSelectedApp()
                        },
                        onLongPress = {
                            if (uiState.indexOfSelectedApp == null) {
                                viewModel.stopTextToSpeech()
                                viewModel.speakTextToSpeech("Please select an app.")
                                return@detectTapGestures
                            }

                            val selectedApp: App =
                                uiState.installedApps[uiState.indexOfSelectedApp!!]

                            setPreferredApp(selectedApp)

                            viewModel.stopTextToSpeech()
                            viewModel.speakTextToSpeech(
                                "Successfully assigned ${selectedApp.appName} as your preferred app." +
                                        " Returning to the gesture drawing screen"
                            )
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
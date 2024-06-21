package com.example.insight.presentation.ui.screens.drawing

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.insight.presentation.ui.shared.App
import com.example.insight.presentation.ui.shared.components.Loading
import kotlinx.coroutines.delay

@Composable
fun DrawingScreen(
    modifier: Modifier = Modifier,
    viewModel: DrawingViewModel = viewModel(),
    preferredApps: List<App>,
    navigateToEnvironmentSensingScreen: () -> Unit,
    navigateToChooseGestureScreen: () -> Unit,
    navigateToAssignPreferredAppScreen: (Int) -> Unit,
) {
    val context: Context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initTextToSpeech(context)
        viewModel.initAudioManager(context)
    }

    LaunchedEffect(uiState.isDrawing) {
        if (!uiState.isDrawing) {
            delay(600)
            viewModel.speakTextToSpeech("Detecting gesture.")
            viewModel.setIsLoading(true)
        }
    }

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) {
            val bitmap = viewModel.getBitmapFromCanvas(context).await()
            viewModel.emptyLines()

            val indexOfGesture: Int = viewModel.getIndexOfResult(context, bitmap).await()

            Log.d("result",indexOfGesture.toString()) // for debugging

            try {
                when (indexOfGesture) {
                    0 -> {
                        viewModel.speakTextToSpeech("Redirecting to google.com")
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("https://google.com")
                        context.startActivity(intent)
                    }
                    1 -> {
//                        navigateToEnvironmentSensingScreen()
                    }
                    2 -> {
                        viewModel.speakTextToSpeech("Redirecting to settings.")
                        val intent = Intent(Settings.ACTION_SETTINGS)
                        context.startActivity(intent)
                    }
                    3 -> {
                        viewModel.speakTextToSpeech("Redirecting to keypad.")
                        val intent = Intent(Intent.ACTION_DIAL)
                        context.startActivity(intent)
                    }
                    4 -> {
                        val intent = context
                            .packageManager
                            .getLaunchIntentForPackage(preferredApps[0].packageName)

                        if (intent != null) {
                            viewModel.speakTextToSpeech("Launching ${preferredApps[0].appName}.")
                            context.startActivity(intent)
                        }
                        else {
                            navigateToAssignPreferredAppScreen(4)
                        }
                    }
                    5 -> {
                        when (viewModel.getRingerMode()) {
                            AudioManager.RINGER_MODE_NORMAL -> {
                                viewModel.speakTextToSpeech("Ringer mode set to vibrate.")
                                viewModel.setRingerMode(AudioManager.RINGER_MODE_VIBRATE)
                            }
                            AudioManager.RINGER_MODE_SILENT -> {
                                viewModel.speakTextToSpeech("Ringer mode set to normal.")
                                viewModel.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
                            }
                            AudioManager.RINGER_MODE_VIBRATE -> {
                                viewModel.speakTextToSpeech("Ringer mode set to normal.")
                                viewModel.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
                            }
                            else -> throw IllegalStateException()
                        }
                    }
                    6 -> {
                        viewModel.speakTextToSpeech("Redirecting to contacts.")
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.type = ContactsContract.Contacts.CONTENT_TYPE
                        context.startActivity(intent)
                    }
                    7 -> {
                        val intent = context
                            .packageManager
                            .getLaunchIntentForPackage(preferredApps[1].packageName)

                        if (intent != null) {
                            viewModel.speakTextToSpeech("Launching ${preferredApps[1].appName}")
                            context.startActivity(intent)
                        }
                        else {
                            navigateToAssignPreferredAppScreen(7)
                        }
                    }
                    8 -> {
                        val intent = context
                            .packageManager
                            .getLaunchIntentForPackage(preferredApps[2].packageName)

                        if (intent != null) {
                            viewModel.speakTextToSpeech("Launching ${preferredApps[2].appName}")
                            context.startActivity(intent)
                        }
                        else {
                            navigateToAssignPreferredAppScreen(8)
                        }
                    }
                    9 -> {
                        viewModel.speakTextToSpeech("Choose a gesture to assign an app to")
                        navigateToChooseGestureScreen()
                    }
                    else -> {
                        viewModel.speakTextToSpeech("Unable to detect gesture, please try again.")
                    }
                }
            } catch (e: Error) {
                e.message?.let { viewModel.speakTextToSpeech(it) }
            }

            viewModel.setIsLoading(false)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.shutdownTextToSpeech()
        }
    }

    if (uiState.isLoading) {
        Loading(
            modifier = modifier
        )
    } else {
        Canvas(
            modifier = modifier
                .pointerInput(true) {
                    detectDragGestures(
                        onDragStart = {
                            viewModel.setIsDrawing(true)
                        },
                        onDragEnd = {
                            viewModel.setIsDrawing(false)
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        viewModel.onDraw(change, dragAmount)
                    }
                }
        ) {
            uiState.lines.forEach { line ->
                drawLine(
                    color = line.color,
                    start = line.start,
                    end = line.end,
                    strokeWidth = line.strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            }
            viewModel.setCanvasSize(size)
        }
    }
}
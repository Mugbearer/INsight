package com.example.insight.presentation.ui.screens.drawing

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.insight.presentation.ui.shared.App
import com.example.insight.presentation.ui.shared.Line
import com.example.insight.presentation.ui.shared.components.Loading
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay

@Composable
fun DrawingScreen(
    modifier: Modifier = Modifier,
    preferredApps: List<App>,
    viewModel: DrawingViewModel = viewModel(),
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
    navigateToEnvironmentSensingScreen: () -> Unit,
    navigateToChooseGestureScreen: () -> Unit,
    navigateToAssignPreferredAppScreen: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    DrawingContent(
        modifier = modifier,
        uiState = uiState,
        preferredApps = preferredApps,
        onDraw = viewModel::onDraw,
        setCanvasSize = viewModel::setCanvasSize,
        emptyLines = viewModel::emptyLines,
        setIsDrawing = viewModel::setIsDrawing,
        setIsLoading = viewModel::setIsLoading,
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
        navigateToEnvironmentSensingScreen = navigateToEnvironmentSensingScreen,
        navigateToChooseGestureScreen = navigateToChooseGestureScreen,
        navigateToAssignPreferredAppScreen = navigateToAssignPreferredAppScreen
    )
}

@Composable
fun DrawingContent(
    modifier: Modifier = Modifier,
    uiState: DrawingUiState,
    preferredApps: List<App>,
    onDraw: (PointerInputChange, Offset) -> Unit,
    setCanvasSize: (Size) -> Unit,
    emptyLines: () -> Unit,
    setIsDrawing: (Boolean) -> Unit,
    setIsLoading: (Boolean) -> Unit,
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
    navigateToEnvironmentSensingScreen: () -> Unit,
    navigateToChooseGestureScreen: () -> Unit,
    navigateToAssignPreferredAppScreen: (Int) -> Unit
) {
    LaunchedEffect(Unit) {
        speakTextToSpeech("You are now in the gesture drawing screen. Please perform a gesture.")
    }

    LaunchedEffect(uiState.isDrawing) {
        if (!uiState.isDrawing) {
            delay(600)
            stopTextToSpeech()
            speakTextToSpeech("Detecting gesture.")
            setIsLoading(true)
        }
    }

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) {
            val bitmap = getBitmapFromCanvas(
                uiState.canvasSize
                ,uiState.lines
            ).await()

            emptyLines()

            val indexOfGesture: Int = getIndexOfResult(bitmap).await()

            Log.d("result",indexOfGesture.toString()) // for debugging

            try {
                when (indexOfGesture) {
                    0 -> {
                        speakTextToSpeech("Redirecting to google.com")
                        redirectToGoogle()
                    }
                    1 -> {
                        navigateToEnvironmentSensingScreen()
                    }
                    2 -> {
                        speakTextToSpeech("Redirecting to settings.")
                        redirectToSettings()
                    }
                    3 -> {
                        speakTextToSpeech("Redirecting to keypad.")
                        redirectToKeypad()
                    }
                    4 -> {
                        if (isExistsApp(0)) {
                            speakTextToSpeech("Launching ${preferredApps[0].appName}.")
                            redirectToPreferredApp(0)
                        }
                        else {
                            navigateToAssignPreferredAppScreen(4)
                        }
                    }
                    5 -> {
                        when (getRingerMode()) {
                            AudioManager.RINGER_MODE_NORMAL -> {
                                speakTextToSpeech("Ringer mode set to vibrate.")
                                setRingerMode(AudioManager.RINGER_MODE_VIBRATE)
                            }
                            AudioManager.RINGER_MODE_SILENT -> {
                                speakTextToSpeech("Ringer mode set to normal.")
                                setRingerMode(AudioManager.RINGER_MODE_NORMAL)
                            }
                            AudioManager.RINGER_MODE_VIBRATE -> {
                                speakTextToSpeech("Ringer mode set to normal.")
                                setRingerMode(AudioManager.RINGER_MODE_NORMAL)
                            }
                            else -> throw IllegalStateException()
                        }
                    }
                    6 -> {
                        speakTextToSpeech("Redirecting to contacts.")
                        redirectToContacts()
                    }
                    7 -> {
                        if (isExistsApp(1)) {
                            speakTextToSpeech("Launching ${preferredApps[0].appName}.")
                            redirectToPreferredApp(1)
                        }
                        else {
                            navigateToAssignPreferredAppScreen(7)
                        }
                    }
                    8 -> {
                        if (isExistsApp(2)) {
                            speakTextToSpeech("Launching ${preferredApps[0].appName}.")
                            redirectToPreferredApp(2)
                        }
                        else {
                            navigateToAssignPreferredAppScreen(8)
                        }
                    }
                    9 -> {
                        speakTextToSpeech("Choose a gesture to assign an app to")
                        navigateToChooseGestureScreen()
                    }
                    else -> {
                        speakTextToSpeech("Unable to detect gesture, please try again.")
                    }
                }
            } catch (e: Error) {
                e.message?.let { speakTextToSpeech(it) }
            }

            setIsLoading(false)
        }
    }

    if (uiState.isLoading) {
        Loading(
            modifier = modifier,
            text = "Detecting Gesture..."
        )
    } else {
        Canvas(
            modifier = modifier
                .pointerInput(true) {
                    detectDragGestures(
                        onDragStart = {
                            setIsDrawing(true)
                        },
                        onDragEnd = {
                            setIsDrawing(false)
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        onDraw(change, dragAmount)
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
            setCanvasSize(size)
        }
    }
}
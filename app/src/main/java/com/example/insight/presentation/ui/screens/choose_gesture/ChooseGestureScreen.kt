package com.example.insight.presentation.ui.screens.choose_gesture

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.insight.presentation.ui.shared.Line
import com.example.insight.presentation.ui.shared.components.Loading
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay

@Composable
fun ChooseGestureScreen(
    modifier: Modifier = Modifier,
    viewModel: ChooseGestureViewModel = viewModel(),
    getBitmapFromCanvas: (Size, List<Line>) -> Deferred<Bitmap>,
    getIndexOfResult: (Bitmap) -> Deferred<Int>,
    speakTextToSpeech: (String) -> Unit,
    stopTextToSpeech: () -> Unit,
    navigateToDrawingScreen: () -> Unit,
    navigateToAssignPreferredAppScreen: (Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    ChooseGestureContent(
        modifier = modifier,
        uiState = uiState,
        onDraw = viewModel::onDraw,
        setCanvasSize = viewModel::setCanvasSize,
        emptyLines = viewModel::emptyLines,
        setIsDrawing = viewModel::setIsDrawing,
        setIsLoading = viewModel::setIsLoading,
        getBitmapFromCanvas = getBitmapFromCanvas,
        getIndexOfResult = getIndexOfResult,
        speakTextToSpeech = speakTextToSpeech,
        stopTextToSpeech = stopTextToSpeech,
        navigateToDrawingScreen = navigateToDrawingScreen,
        navigateToAssignPreferredAppScreen = navigateToAssignPreferredAppScreen
    )
}

@Composable
fun ChooseGestureContent(
    modifier: Modifier = Modifier,
    uiState: ChooseGestureUiState,
    onDraw: (PointerInputChange, Offset) -> Unit,
    setCanvasSize: (Size) -> Unit,
    emptyLines: () -> Unit,
    setIsDrawing: (Boolean) -> Unit,
    setIsLoading: (Boolean) -> Unit,
    getBitmapFromCanvas: (Size, List<Line>) -> Deferred<Bitmap>,
    getIndexOfResult: (Bitmap) -> Deferred<Int>,
    speakTextToSpeech: (String) -> Unit,
    stopTextToSpeech: () -> Unit,
    navigateToDrawingScreen: () -> Unit,
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
                uiState.canvasSize,
                uiState.lines
            ).await()
            emptyLines()

            val indexOfGesture: Int = getIndexOfResult(bitmap).await()

            Log.d("result",indexOfGesture.toString()) // for debugging

            if (listOf(4,7,8).contains(indexOfGesture)){
                navigateToAssignPreferredAppScreen(indexOfGesture)
            } else if (indexOfGesture == 10) {
                speakTextToSpeech("Unable to detect gesture, please try again.")
            }
            else {
                speakTextToSpeech(
                    "Invalid. This is a reserved gesture. Navigating back to the gesture drawing screen."
                )
                navigateToDrawingScreen()
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
package com.example.insight.presentation.ui.screens.choose_gesture

import android.content.Context
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
import com.example.insight.presentation.ui.shared.components.Loading
import kotlinx.coroutines.delay

@Composable
fun ChooseGestureScreen(
    modifier: Modifier = Modifier,
    viewModel: ChooseGestureViewModel = viewModel(),
    navigateToDrawingScreen: () -> Unit,
    navigateToAssignPreferredAppScreen: (Int) -> Unit
) {
    val context: Context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initTextToSpeech(context)
    }

    LaunchedEffect(uiState.isDrawing) {
        if (!uiState.isDrawing) {
            delay(600)
            viewModel.textToSpeechSpeak("Detecting gesture.")
            viewModel.setIsLoading(true)
        }
    }

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) {
            val bitmap = viewModel.getBitmapFromCanvas(context).await()
            viewModel.emptyLines()

            val indexOfGesture: Int = viewModel.getIndexOfResult(context, bitmap).await()

            Log.d("result",indexOfGesture.toString()) // for debugging

            if (listOf(4,7,8).contains(indexOfGesture)){
                navigateToAssignPreferredAppScreen(indexOfGesture)
            }
            else {
                viewModel.textToSpeechSpeak(
                    "Invalid. This is a reserved gesture. Navigating back to the gesture drawing screen."
                )
                navigateToDrawingScreen()
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
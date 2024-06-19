package com.example.insight.presentation.ui.screens.drawing

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DrawingScreen(
    modifier: Modifier = Modifier,
    viewModel: DrawingViewModel = viewModel(),
    navigateToEnvironmentSensingScreen: () -> Unit,
    navigateToChooseGestureScreen: () -> Unit,
    navigateToAssignPreferredAppScreen: (Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    val context: Context = LocalContext.current

    LaunchedEffect(Unit) {
        //
    }

    Canvas(
        modifier = modifier
            .pointerInput(true) {
                detectDragGestures { change, dragAmount ->
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
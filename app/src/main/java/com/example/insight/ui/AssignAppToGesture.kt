package com.example.insight.ui

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.example.insight.state.Line
import com.example.insight.state.helperfunctions.useTts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun AssignAppToGesture(
    modifier: Modifier = Modifier,
    isDrawing: Boolean,
    setIsDrawing: (Boolean) -> Unit,
    addLine: (PointerInputChange, Offset) -> Unit,
    lines: List<Line>,
    detectGesture: (Context, Size) -> Int,
    navigateToAssignPreferredApp: (Context, Int) -> Unit,
    navigateToStartScreen: () -> Unit
) {
    val context: Context = LocalContext.current
    var canvasSize: Size = Size.Zero

    LaunchedEffect(isDrawing) {
        if (!isDrawing && lines.isNotEmpty()) {
            delay(500)
            val indexOfClass: Int = withContext(Dispatchers.Default) {
                detectGesture(context, canvasSize)
            }
            if (listOf(4,7,8).contains(indexOfClass)){
                context.useTts("Choose the app to be assigned")
                navigateToAssignPreferredApp(context, indexOfClass)
            }
            else {
                context.useTts(
                    "Invalid. This is a reserved gesture. Navigating back to start screen."
                )
                navigateToStartScreen()
            }
        }
    }

    Canvas(
        modifier = modifier
            .pointerInput(true) {
                detectDragGestures(
                    onDragStart = { setIsDrawing(true) },
                    onDragEnd = { setIsDrawing(false) },
                ) { change, dragAmount ->
                    addLine(change, dragAmount)
                }
            }
    ) {
        lines.forEach { line ->
            drawLine(
                color = line.color,
                start = line.start,
                end = line.end,
                strokeWidth = line.strokeWidth.toPx(),
                cap = StrokeCap.Round
            )
        }

        canvasSize = size
    }
}
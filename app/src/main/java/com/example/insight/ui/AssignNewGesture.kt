package com.example.insight.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
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
fun AssignNewGesture(
    modifier: Modifier = Modifier,
    oldGesture: Int,
    isDrawing: Boolean,
    setIsDrawing: (Boolean) -> Unit,
    addLine: (PointerInputChange, Offset) -> Unit,
    lines: List<Line>,
    detectGesture: (Context, Size) -> Int,
    swapGestures: (Int, Int) -> Unit,
    navigateToStartScreen: () -> Unit,
) {
    val context: Context = LocalContext.current

    var canvasSize: Size = Size.Zero

    LaunchedEffect(isDrawing) {
        if (!isDrawing && lines.isNotEmpty()) {
            delay(500)
            val indexOfClass: Int = withContext(Dispatchers.Default) {
                detectGesture(context, canvasSize)
            }
            swapGestures(oldGesture, indexOfClass)
            context.useTts("Gesture reassigned successfully")
            navigateToStartScreen()
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
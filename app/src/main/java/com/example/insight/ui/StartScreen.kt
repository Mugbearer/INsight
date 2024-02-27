package com.example.insight.ui

import android.content.Context
import android.widget.Toast
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
import kotlinx.coroutines.delay

@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    isDrawing: Boolean,
    setIsDrawing: (Boolean) -> Unit,
    addLine: (PointerInputChange, Offset) -> Unit,
    lines: List<Line>,
    detectGesture: (Context, Size) -> String,
) {
    val context: Context = LocalContext.current

    var canvasSize: Size = Size.Zero

    LaunchedEffect(isDrawing) {
        if (!isDrawing) {
//            setIsDetectingGesture(true)
            delay(1200)
            val x = detectGesture(context, canvasSize)
            Toast.makeText(context, x, Toast.LENGTH_SHORT).show()
            context.useTts(x)
//            setIsDetectingGesture(false)
        }
    }

    Canvas(
        modifier = modifier
            .pointerInput(true) {
                detectDragGestures(
                    onDragStart = { setIsDrawing(true) },
                    onDragEnd = { setIsDrawing(false) }
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
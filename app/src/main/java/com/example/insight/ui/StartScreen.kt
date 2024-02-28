package com.example.insight.ui

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
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
import com.example.insight.state.helperfunctions.IntentActionHelper
import com.example.insight.state.helperfunctions.useTts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    isDrawing: Boolean,
    setIsDrawing: (Boolean) -> Unit,
    addLine: (PointerInputChange, Offset) -> Unit,
    lines: List<Line>,
    detectGesture: (Context, Size) -> Int,
    environmentSensingBitmap: Bitmap?,
    setEnvironmentSensingBitmap: (Bitmap?) -> Unit,
    navigateToEnvironmentSensing: () -> Unit
) {
    val context: Context = LocalContext.current

    var canvasSize: Size = Size.Zero

    val loadImage = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) {
        setEnvironmentSensingBitmap(it)
//        val resultDebug: String = (it!=null).toString()
//        Log.d("bitmap state",resultDebug)
    }

    LaunchedEffect(isDrawing) {
        if (!isDrawing) {
            delay(1200)
            val indexOfClass: Int = withContext(Dispatchers.Default) {
                detectGesture(context, canvasSize)
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(context, indexOfClass.toString(), Toast.LENGTH_SHORT).show()
                context.useTts(indexOfClass.toString())

                when (indexOfClass) {
                    0 -> {
                        IntentActionHelper.launchPhoneInterface(context)
                    }
                    1 -> {
                        IntentActionHelper.launchBrowser(
                            context,
                            "google.com"
                        )
                    }
                    2 -> {
                        loadImage.launch()

                        if (environmentSensingBitmap != null) {
                            navigateToEnvironmentSensing()
                        }
                    }
                    3 -> {
                        IntentActionHelper.launchEmailApp(context)
                    }
                    else -> {

                    }
                }
            }
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
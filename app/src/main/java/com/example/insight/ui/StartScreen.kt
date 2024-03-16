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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.example.insight.data.ClassNames
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
    setEnvironmentSensingBitmap: (Bitmap?) -> Unit,
    navigateToEnvironmentSensing: () -> Unit,
    preferredApp: String,
    navigateToPreferredApp: (Context) -> Unit,
    navigateToAssignOldGesture: () -> Unit
) {
    val context: Context = LocalContext.current

    var canvasSize: Size = Size.Zero

    val loadImage = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) {
        val msg = if (it == null) {
            "bitmap is null"
        } else {
            "bitmap is not null"
        }
        Log.d("test",msg)
        if (it != null) {
            setEnvironmentSensingBitmap(it)
            Log.d("test","3")
            navigateToEnvironmentSensing()
            Log.d("test","4")
        }
    }

    LaunchedEffect(isDrawing) {
        if (!isDrawing && lines.isNotEmpty()) {
            delay(500)
            val indexOfClass: Int = withContext(Dispatchers.Default) {
                detectGesture(context, canvasSize)
            }
            withContext(Dispatchers.Main) {
                Log.d("index", "$indexOfClass: ${ClassNames.list()[indexOfClass]}")
                when (indexOfClass) {
                    0 -> {
                        context.useTts("Redirecting to google.com")
                        IntentActionHelper.launchBrowser(
                            context,
                            "https://google.com"
                        )

                    }
                    1 -> {
                        context.useTts("Redirecting to environment sensing")
                        loadImage.launch()
                    }
                    2 -> {
                        /*TODO: settings*/
                    }
                    3 -> {
                        context.useTts("Redirecting to keypad")
                        IntentActionHelper.launchPhoneInterface(context)
                    }
                    4 -> {
                        if (preferredApp != ""){
                            context.useTts("Launching preferred app")
                            IntentActionHelper.launchPreferredApp(
                                context,
                                preferredApp
                            )
                        }
                        else {
                            context.useTts("Choose your preferred app")
                            navigateToPreferredApp(context)
                        }
                    }
                    5 -> {

                    }
                    6 -> {

                    }
                    7 -> {
                        context.useTts("Redirecting to email")
                        IntentActionHelper.launchEmailApp(context)
                    }
                    8 -> {

                    }
                    9 -> {
                        context.useTts("Please choose gesture to be changed")
                        navigateToAssignOldGesture()
                    }
                    else -> {
                        context.useTts("Please repeat")
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
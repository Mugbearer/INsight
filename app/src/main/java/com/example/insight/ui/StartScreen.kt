package com.example.insight.ui

import android.content.Context
import android.graphics.Bitmap
import android.media.AudioManager
import android.util.Log
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
import com.example.insight.data.App
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
    preferredApps: List<App>,
    navigateToAssignAppToGesture: () -> Unit,
    navigateToPreferredApp: (Context, Int) -> Unit,
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

    val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

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
                        context.useTts("Redirecting to settings")
                        IntentActionHelper.launchSettings(context)
                    }
                    3 -> {
                        context.useTts("Redirecting to keypad")
                        IntentActionHelper.launchPhoneInterface(context)
                    }
                    4 -> {
                        val intent = IntentActionHelper.launchPreferredAppIntent(
                            context = context,
                            packageName = preferredApps[0].packageName
                        )

                        if (intent != null) {
                            context.useTts("Launching ${preferredApps[0].appName}")
                            context.startActivity(intent)
                        }
                        else {
                            navigateToPreferredApp(context,4)
                        }
                    }
                    5 -> {
                        when (audioManager.ringerMode) {
                            AudioManager.RINGER_MODE_NORMAL -> {
                                context.useTts("Ringer mode set to vibrate")
                                audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                            }
                            AudioManager.RINGER_MODE_SILENT -> {
                                context.useTts("Ringer mode set to normal")
                                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                            }
                            AudioManager.RINGER_MODE_VIBRATE -> {
                                context.useTts("Ringer mode set to normal")
                                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                            }
                            else -> throw IllegalStateException()
                        }
                    }
                    6 -> {
                        context.useTts("Redirecting to contacts")
                        IntentActionHelper.launchContacts(context)
                    }
                    7 -> {
                        val intent = IntentActionHelper.launchPreferredAppIntent(
                            context = context,
                            packageName = preferredApps[1].packageName
                        )

                        if (intent != null) {
                            context.useTts("Launching ${preferredApps[1].appName}")
                            context.startActivity(intent)
                        }
                        else {
                            navigateToPreferredApp(context,7)
                        }
                    }
                    8 -> {
                        val intent = IntentActionHelper.launchPreferredAppIntent(
                            context = context,
                            packageName = preferredApps[2].packageName
                        )

                        if (intent != null) {
                            context.useTts("Launching ${preferredApps[2].appName}")
                            context.startActivity(intent)
                        }
                        else {
                            navigateToPreferredApp(context,8)
                        }
                    }
                    9 -> {
                        context.useTts("Choose a gesture to assign an app to")
                        navigateToAssignAppToGesture()
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
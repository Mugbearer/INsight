package com.example.insight.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.example.insight.state.helperfunctions.useTts

@Composable
fun EnvironmentSensingScreen(
    modifier: Modifier = Modifier,
    environmentSensingBitmap: Bitmap,
    senseEnvironment: (Context) -> String,
    environmentResults: String,
    setEnvironmentResults: (String) -> Unit,
    navigateToStartScreen: () -> Unit
) {
    val context: Context = LocalContext.current

    LaunchedEffect(Unit) {
        setEnvironmentResults("Loading")
        val results = senseEnvironment(context)
        context.useTts(results)
    }

    Column(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures (
                    onDragStart = {
                        navigateToStartScreen()
                    },
                    onDrag = { _, _ ->  }
                )
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(environmentResults)
    }
}
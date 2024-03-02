package com.example.insight.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

@Composable
fun EnvironmentSensingScreen(
    modifier: Modifier = Modifier,
    environmentSensingBitmap: Bitmap,
    senseEnvironment: (Context) -> Unit,
    navigateToStartScreen: () -> Unit
) {
    val context: Context = LocalContext.current

    LaunchedEffect(Unit) {
        senseEnvironment(context)
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
        Image(
            bitmap = environmentSensingBitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}
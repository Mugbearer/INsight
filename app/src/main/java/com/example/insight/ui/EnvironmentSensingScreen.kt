package com.example.insight.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale

@Composable
fun EnvironmentSensingScreen(
    modifier: Modifier = Modifier,
    environmentSensingBitmap: Bitmap?,
    navigateToStartScreen: () -> Unit
    ) {
    val bitmapHolder = environmentSensingBitmap?.asImageBitmap()

    Column(
        modifier = modifier
            .clickable {
                navigateToStartScreen()
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (bitmapHolder != null) {
            Image(
                bitmap = bitmapHolder,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Text(
                text = "No Bitmap"
            )
        }
    }
}
package com.example.insight.presentation.ui.screens.tutorial

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.insight.R
import java.util.Locale

@Composable
fun TutorialScreen(
    modifier: Modifier = Modifier,
    speakTextToSpeech: (String) -> Unit,
    stopTextToSpeech: () -> Unit,
    navigateToDrawingScreen: () -> Unit
) {
    val instructions: String =  stringResource(id = R.string.tutorial)

    LaunchedEffect(Unit) {
        stopTextToSpeech()
        speakTextToSpeech(instructions)
    }

    Column(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        stopTextToSpeech()
                        navigateToDrawingScreen()
                    },
                    onLongPress = {
                        stopTextToSpeech()
                        speakTextToSpeech(instructions)
                    }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(instructions)
    }
}
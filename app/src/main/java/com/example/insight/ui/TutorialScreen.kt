package com.example.insight.ui

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
    navigateToStartScreen: (Context) -> Unit
) {
    val context: Context = LocalContext.current
    val instructions: String =  stringResource(id = R.string.instructions)

    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(Unit) {
        textToSpeech = TextToSpeech(
            context
        ) {
            if (it == TextToSpeech.SUCCESS) {
                textToSpeech?.let { txtToSpeech ->
                    txtToSpeech.language = Locale.US
                    txtToSpeech.setSpeechRate(0.8f)
                    txtToSpeech.speak(
                        instructions,
                        TextToSpeech.QUEUE_ADD,
                        null,
                        null
                    )
                }
            }
        }
    }

    Column(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        Log.d("onDoubleTap","onDoubleTap")
                        textToSpeech?.stop() // Stop TTS on double tap
                        navigateToStartScreen(context)
                    },
                    onLongPress = {
                        Log.d("onLongPress","onLongPress")
                        textToSpeech?.speak(
                            instructions,
                            TextToSpeech.QUEUE_ADD,
                            null,
                            null
                        )
                    }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = instructions)
    }

    DisposableEffect(Unit) {
        onDispose {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }
}

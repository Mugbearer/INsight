package com.example.insight.state.helperfunctions

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

fun Context.useTts(text: String) {
    var textToSpeech: TextToSpeech? = null
    textToSpeech = TextToSpeech(
        this
    ) {
        if (it == TextToSpeech.SUCCESS) {
            textToSpeech?.let { txtToSpeech ->
                txtToSpeech.language = Locale.US
                txtToSpeech.setSpeechRate(1.0f)
                txtToSpeech.speak(
                    text,
                    TextToSpeech.QUEUE_ADD,
                    null,
                    null
                )
            }
        }
    }
}
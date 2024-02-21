package com.example.insight.state

import android.content.Context
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.lifecycle.ViewModel
import com.example.insight.state.helperfunctions.ImageClassificationHelper
import com.example.insight.state.helperfunctions.ImageClassificationHelper.drawToBitmap
import com.example.insight.state.helperfunctions.ImageClassificationHelper.findIndexOfMaxValue
import com.example.insight.state.helperfunctions.IntentActionHelper
import java.util.Locale

class GestureViewModel : ViewModel() {

    private val _uiState = mutableStateOf(GestureUiState())
    val uiState: State<GestureUiState> = _uiState

    fun addLine(change: PointerInputChange, dragAmount: Offset) {
        change.consume()

        val line = Line(
            start = change.position - dragAmount,
            end = change.position
        )

        _uiState.value = _uiState.value.copy(
            lines = _uiState.value.lines.toMutableList().apply { add(line) }
        )
    }

    fun setIsDrawing(isDrawing: Boolean) {
        _uiState.value = _uiState.value.copy(
            isDrawing = isDrawing
        )
    }

    fun detectGesture(context: Context, canvasSize: Size) {
        val bitmap: Bitmap = drawToBitmap(
            context = context,
            canvasSize = canvasSize,
            lines = uiState.value.lines
        )

        val outputFloatArray: FloatArray = ImageClassificationHelper
            .classifyImageAndGetProbabilities(
                context = context,
                bitmap = bitmap
            )

        val maxIndex: Int = findIndexOfMaxValue(outputFloatArray)

        if (outputFloatArray[maxIndex] < 2) {
//            Toast.makeText(context, "less than 2", Toast.LENGTH_SHORT).show()

            useTts(context,"Please repeat")
        } else {
            performAction(context, maxIndex)
        }

        emptyLines()
    }

    private fun emptyLines() {
        _uiState.value = _uiState.value.copy(
            lines = mutableListOf()
        )
    }

    private fun performAction(context: Context, index: Int) {
        when (index) {
            0 -> {
                useTts(context,"Redirecting to keypad")
                IntentActionHelper.launchPhoneInterface(context)
            }
            1 -> {
                useTts(context,"Redirecting to email")
                IntentActionHelper.launchEmailApp(context)
            }
            2 -> {
                useTts(context,"Redirecting to google")
                IntentActionHelper.launchBrowser(context,"google.com")
            }
            else -> {
                useTts(context,"Redirecting to camera")
                IntentActionHelper.launchCamera(context)
            }
        }
    }

    private fun useTts(context: Context, text: String) {
        var textToSpeech: TextToSpeech? = null
        textToSpeech = TextToSpeech(
            context
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
}
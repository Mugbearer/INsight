package com.example.insight.state

import android.content.Context
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.lifecycle.ViewModel
import com.example.insight.data.ClassNames
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

    fun detectGesture(context: Context, canvasSize: Size): String {
        Log.d("Test","1")
        val bitmap: Bitmap = drawToBitmap(
            context = context,
            canvasSize = canvasSize,
            lines = uiState.value.lines
        )

        emptyLines()

        Log.d("Test","2")

        val outputFloatArray: FloatArray = ImageClassificationHelper
            .classifyImageAndGetProbabilities(
                context = context,
                bitmap = bitmap
            )

        val maxIndex: Int = findIndexOfMaxValue(outputFloatArray)

        Log.d("Test","3")

//        if (outputFloatArray[maxIndex] < 2 && false) {
//            useTts(context,"Please repeat")
//
//            return null
//        } else {
////            Toast.makeText(context, ClassNames.classNames()[maxIndex], Toast.LENGTH_SHORT).show()
////            Toast.makeText(context, ClassNames.classNames()[maxIndex] + ": " + outputFloatArray[maxIndex].toString(), Toast.LENGTH_SHORT).show()
////            performAction(context, maxIndex)
//
//            return ClassNames.classNames()[maxIndex] + ": " + outputFloatArray[maxIndex].toString()
//        }

        return ClassNames.classNames()[maxIndex] + ": " + outputFloatArray[maxIndex].toString()
    }

    private fun emptyLines() {
        _uiState.value = _uiState.value.copy(
            lines = mutableListOf()
        )
    }

    private fun performAction(context: Context, index: Int) {
        when (index) {
            0 -> {
                IntentActionHelper.launchPhoneInterface(context)
            }
            1 -> {
                IntentActionHelper.launchEmailApp(context)
            }
            2 -> {
                IntentActionHelper.launchBrowser(context,"google.com")
            }
            else -> {
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
package com.example.insight.state

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.lifecycle.ViewModel
import com.example.insight.ml.Yolov8mFloat32
import com.example.insight.state.helperfunctions.GestureModelHelper
import com.example.insight.state.helperfunctions.GestureModelHelper.drawToBitmap
import com.example.insight.state.helperfunctions.GestureModelHelper.findIndexOfMaxValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.tensorflow.lite.support.image.TensorImage

class AppViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GestureUiState())
    val uiState: StateFlow<GestureUiState> = _uiState.asStateFlow()

    fun addLine(change: PointerInputChange, dragAmount: Offset) {
        change.consume()

        val line = Line(
            start = change.position - dragAmount,
            end = change.position
        )

        _uiState.update { currentState ->
            currentState.copy(
                lines = currentState.lines.toMutableList().apply { add(line) }
            )
        }
    }

    fun setIsDrawing(isDrawing: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isDrawing = isDrawing
            )
        }
    }

    fun detectGesture(context: Context, canvasSize: Size): Int {
        val bitmap: Bitmap = drawToBitmap(
            context = context,
            canvasSize = canvasSize,
            lines = uiState.value.lines
        )

        emptyLines()

        val outputFloatArray: FloatArray = GestureModelHelper
            .classifyImageAndGetProbabilities(
                context = context,
                bitmap = bitmap
            )

        return findIndexOfMaxValue(outputFloatArray)
    }

    private fun emptyLines() {
        _uiState.update { currentState ->
            currentState.copy(
                lines = mutableListOf()
            )
        }
    }

    fun senseEnvironment(context: Context) {
        val model = Yolov8mFloat32.newInstance(context)

        val image = TensorImage.fromBitmap(uiState.value.environmentSensingBitmap)

        val outputs = model.process(image)
        val output = outputs.javaClass

        Log.d("test output",output.toString())

        model.close()
    }

    fun setEnvironmentSensingBitmap(bitmap: Bitmap?) {
        _uiState.update { currentState ->
            currentState.copy(
                environmentSensingBitmap = bitmap
            )
        }
    }
}
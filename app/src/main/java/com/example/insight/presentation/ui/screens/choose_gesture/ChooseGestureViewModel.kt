package com.example.insight.presentation.ui.screens.choose_gesture

import android.speech.tts.TextToSpeech
import androidx.annotation.VisibleForTesting
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.lifecycle.ViewModel
import com.example.insight.presentation.ui.shared.Line
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ChooseGestureUiState(
    val canvasSize: Size = Size.Zero,
    val lines: MutableList<Line> = mutableListOf(),
    val isDrawing: Boolean = true,
    val isLoading: Boolean = false
)

class ChooseGestureViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChooseGestureUiState())
    val uiState = _uiState.asStateFlow()

    fun onDraw(change: PointerInputChange, dragAmount: Offset) {
        val line: Line = getLine(change, dragAmount)
        addLine(line)
    }

    fun setCanvasSize(canvasSize: Size) {
        _uiState.update { currentState ->
            currentState.copy(
                canvasSize = canvasSize
            )
        }
    }

    @VisibleForTesting
    internal fun getLine(change: PointerInputChange, dragAmount: Offset): Line {
        return Line(
            start = change.position - dragAmount,
            end = change.position
        )
    }

    @VisibleForTesting
    internal fun addLine(line: Line) {
        _uiState.update { currentState ->
            currentState.copy(
                lines = currentState.lines.toMutableList().apply { add(line) }
            )
        }
    }

    fun emptyLines() {
        _uiState.update { currentState ->
            currentState.copy(
                lines = mutableListOf()
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

    fun setIsLoading(isLoading: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = isLoading
            )
        }
    }
}
package com.example.insight.presentation.ui.screens.drawing

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.insight.domain.gesture.GestureHandler
import com.example.insight.presentation.ui.shared.Line
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DrawingUiState(
    val canvasSize: Size = Size.Zero,
    val lines: MutableList<Line> = mutableListOf(),
    val isLoading: Boolean = false
)

class DrawingViewModel(
    private val gestureHandler: GestureHandler = GestureHandler()
) : ViewModel() {
    private val _uiState = MutableStateFlow(DrawingUiState())
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

    @VisibleForTesting
    internal fun emptyLines() {
        _uiState.update { currentState ->
            currentState.copy(
                lines = mutableListOf()
            )
        }
    }

    fun getIndexOfResult(context: Context): Deferred<Int> {
        return viewModelScope.async {
            gestureHandler.getIndexOfResult(
                context = context,
                canvasSize = uiState.value.canvasSize,
                lines = uiState.value.lines
            )
        }
    }
}
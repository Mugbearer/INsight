package com.example.insight.state

data class GestureUiState(
    val isDrawing: Boolean = false,
    val lines: MutableList<Line> = mutableListOf()
)

package com.example.insight.state

import android.graphics.Bitmap

data class GestureUiState(
    val isDrawing: Boolean = false,
    val lines: MutableList<Line> = mutableListOf(),
    val environmentSensingBitmap: Bitmap? = null
)

package com.example.insight.state

import android.graphics.Bitmap

data class GestureUiState(
    val isDrawing: Boolean = true,
    val lines: MutableList<Line> = mutableListOf(),
    val environmentSensingBitmap: Bitmap? = null
)

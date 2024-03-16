package com.example.insight.state

import android.graphics.Bitmap
import com.example.insight.data.App

data class GestureUiState(
    val isDrawing: Boolean = true,
    val lines: MutableList<Line> = mutableListOf(),
    val environmentSensingBitmap: Bitmap? = null,
    val environmentResults: String = "Loading",
    val preferredApps: List<App> = listOf(),
    val listOfInstalledApps: List<App> = listOf(),
    val indexOfSelectedApp: Int? = null,
    val selectedApp: App? = null
)

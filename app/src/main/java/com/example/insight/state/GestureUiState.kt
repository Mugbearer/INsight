package com.example.insight.state

import android.graphics.Bitmap

data class GestureUiState(
    val isDrawing: Boolean = true,
    val lines: MutableList<Line> = mutableListOf(),
    val environmentSensingBitmap: Bitmap? = null,
//    val environmentResults: MutableList<String> = mutableListOf()
    val environmentResults: String = "Loading",
    val preferredApp: String = "",
    val gestureMap: MutableMap<Int, Int> = mutableMapOf(
        0 to 0,
        1 to 1,
        2 to 2,
        3 to 3,
        4 to 4,
        5 to 5,
        6 to 6,
        7 to 7,
        8 to 8,
        9 to 9
    ),
    val mapOfInstalledApps: Map<String, String> = mapOf(),
    val listOfInstalledApps: List<String> = mapOfInstalledApps.keys.toList(),
    val indexOfSelectedApp: Int? = null,
    val selectedApp: String? = null
)

package com.example.insight.state

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.insight.INsightApplication
import com.example.insight.data.UserPreferencesRepository
import com.example.insight.state.helperfunctions.EnvironmentSensingHelper
import com.example.insight.state.helperfunctions.GestureModelHelper
import com.example.insight.state.helperfunctions.GestureModelHelper.drawToBitmap
import com.example.insight.state.helperfunctions.GestureModelHelper.findIndexOfMaxValue
import com.example.insight.state.helperfunctions.useTts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GestureUiState())

//    val uiState: StateFlow<GestureUiState> =
//        userPreferencesRepository.preferredApp.map { preferredApp ->
//            GestureUiState(preferredApp = preferredApp)
//        }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5_000),
//            initialValue = GestureUiState()
//        )

    val uiState: StateFlow<GestureUiState> = combine(
        userPreferencesRepository.preferredApp,
        userPreferencesRepository.gestureMap,
        _uiState
    ) { preferredApp, gestureMap, gestureUiState ->
        gestureUiState.copy(
            preferredApp = preferredApp,
            gestureMap = gestureMap
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GestureUiState()
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as INsightApplication)
                AppViewModel(application.userPreferencesRepository)
            }
        }
    }

    init {
        viewModelScope.launch {
            userPreferencesRepository.clearPreferences()
        }
    }

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

        return uiState.value.gestureMap[findIndexOfMaxValue(outputFloatArray)]!!
    }

    private fun emptyLines() {
        _uiState.update { currentState ->
            currentState.copy(
                lines = mutableListOf()
            )
        }
    }

    fun senseEnvironment(context: Context): String {
        val results = EnvironmentSensingHelper.getEnvironmentSensingOutput(
            context,
            uiState.value.environmentSensingBitmap!!
        )

        setEnvironmentResults(results)

        return results
    }

    fun setEnvironmentResults(results: String) {
        _uiState.update { currentState ->
            currentState.copy(
                environmentResults = results
            )
        }
    }

    fun setEnvironmentSensingBitmap(bitmap: Bitmap?) {
        _uiState.update { currentState ->
            currentState.copy(
                environmentSensingBitmap = bitmap
            )
        }
    }

    fun getMapOfApps(context: Context) {
        val applicationInfoList: List<ApplicationInfo> = context
            .packageManager
            .getInstalledApplications(PackageManager.GET_META_DATA)

        val mapOfInstalledApps: MutableMap<String, String> = mutableMapOf()

        applicationInfoList.forEach { appInfo ->
            val launchIntent = context.packageManager.getLaunchIntentForPackage(appInfo.packageName)
            if (launchIntent != null) {
                val appName = context.packageManager.getApplicationLabel(appInfo).toString()
                mapOfInstalledApps[appName] = appInfo.packageName
            }
        }

        _uiState.update { currentState ->
            currentState.copy(
                mapOfInstalledApps = mapOfInstalledApps,
                listOfInstalledApps = mapOfInstalledApps.keys.toList()
            )
        }
    }

    fun navigateToNextButton(): String {
        val newIndex = when (val oldIndex: Int? = uiState.value.indexOfSelectedApp) {
            null -> {
                0
            }
            (uiState.value.listOfInstalledApps.size - 1) -> {
                0
            }
            else -> {
                oldIndex + 1
            }
        }

        Log.d("newIndex","newIndex: $newIndex")
        setIndexOfSelectedApp(newIndex)
        Log.d("newIndex","newIndex state: ${uiState.value.indexOfSelectedApp}")

        return uiState.value.listOfInstalledApps[newIndex]
    }

    fun navigateToPreviousButton(): String {
        val maxIndex: Int = uiState.value.listOfInstalledApps.size - 1
        val newIndex = when (val oldIndex: Int? = uiState.value.indexOfSelectedApp) {
            null -> maxIndex
            0 -> maxIndex
            else -> oldIndex - 1
        }

        Log.d("newIndex","newIndex: $newIndex")
        setIndexOfSelectedApp(newIndex)

        return uiState.value.listOfInstalledApps[newIndex]
    }

    private fun setIndexOfSelectedApp(index: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                indexOfSelectedApp = index,
                selectedApp = uiState.value.listOfInstalledApps[index]
            )
        }
    }

    fun setPreferredApp(context: Context) {
        if (uiState.value.selectedApp == null) {
            navigateToNextButton()
            context.useTts(uiState.value.selectedApp!!)
        }
        else{
            viewModelScope.launch {
                userPreferencesRepository.setPreferredApp(
                    uiState.value.mapOfInstalledApps[
                        uiState.value.listOfInstalledApps[
                            uiState.value.indexOfSelectedApp!!
                        ]
                    ]!!
                )

                _uiState.update { currentState ->
                    currentState.copy(
                        mapOfInstalledApps = mapOf(),
                        listOfInstalledApps = listOf(),
                        indexOfSelectedApp = null
                    )
                }
                context.useTts("${uiState.value.selectedApp} assigned as preferred app")
            }
        }
    }

    fun swapGestures(firstGesture: Int, secondGesture: Int) {
        viewModelScope.launch {
            userPreferencesRepository.swapGestures(firstGesture,secondGesture)
        }
    }
}
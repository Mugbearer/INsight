package com.example.insight.presentation.ui.screens.assign_preferred_app

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import com.example.insight.presentation.ui.shared.App
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AssignPreferredAppUiState(
    val installedApps: MutableList<App> = mutableListOf(),
    val indexOfSelectedApp: Int? = null,
    val isLoading: Boolean = true
)

class AssignPreferredAppViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AssignPreferredAppUiState())
    val uiState = _uiState.asStateFlow()

    fun setInstalledApps(installedApps: List<App>) {
        _uiState.update { currentState ->
            currentState.copy(
                installedApps = installedApps as MutableList<App>
            )
        }
    }

    @VisibleForTesting
    internal fun setIndexOfSelectedApp(indexOfSelectedApp: Int?) {
        _uiState.update { currentState ->
            currentState.copy(
                indexOfSelectedApp = indexOfSelectedApp
            )
        }
    }

    fun selectNextAppAndReturnAppName(): String {
        val newIndex = when (val oldIndex: Int? = uiState.value.indexOfSelectedApp) {
            null -> {
                0
            }
            (uiState.value.installedApps.size - 1) -> {
                0
            }
            else -> {
                oldIndex + 1
            }
        }

        setIndexOfSelectedApp(newIndex)

        return uiState.value.installedApps[uiState.value.indexOfSelectedApp!!].appName
    }

    fun selectPreviousAppAndReturnAppName(): String {
        val maxIndex: Int = uiState.value.installedApps.size - 1
        val newIndex = when (val oldIndex: Int? = uiState.value.indexOfSelectedApp) {
            null -> maxIndex
            0 -> maxIndex
            else -> oldIndex - 1
        }

        setIndexOfSelectedApp(newIndex)

        return uiState.value.installedApps[uiState.value.indexOfSelectedApp!!].appName
    }

    fun setIsLoading(isLoading: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = isLoading
            )
        }
    }
}
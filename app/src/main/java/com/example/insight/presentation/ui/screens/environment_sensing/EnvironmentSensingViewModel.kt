package com.example.insight.presentation.ui.screens.environment_sensing

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EnvironmentSensingUiState(
    val result: String? = null
)

class EnvironmentSensingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EnvironmentSensingUiState())
    val uiState = _uiState.asStateFlow()

    fun setResult(result: String?) {
        _uiState.update { currentState ->
            currentState.copy(
                result = result ?: "Unable to detect objects."
            )
        }
    }
}
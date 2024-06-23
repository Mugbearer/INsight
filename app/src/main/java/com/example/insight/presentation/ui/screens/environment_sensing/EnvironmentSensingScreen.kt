package com.example.insight.presentation.ui.screens.environment_sensing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EnvironmentSensingScreen(
    modifier: Modifier = Modifier,
    viewModel: EnvironmentSensingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
}

@Composable
fun EnvironmentSensingContent(
    modifier: Modifier = Modifier,
    uiState: EnvironmentSensingViewModel
) {

}
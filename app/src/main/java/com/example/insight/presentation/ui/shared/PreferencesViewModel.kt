package com.example.insight.presentation.ui.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.insight.INsightApplication
import com.example.insight.data.UserPreferencesRepository

class PreferencesViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as INsightApplication)
                PreferencesViewModel(application.userPreferencesRepository)
            }
        }
    }
}
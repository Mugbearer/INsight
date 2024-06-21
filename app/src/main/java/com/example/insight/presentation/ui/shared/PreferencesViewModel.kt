package com.example.insight.presentation.ui.shared

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.insight.INsightApplication
import com.example.insight.data.UserPreferencesRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.checkerframework.checker.units.qual.A

data class PreferencesUiState(
    val preferredApps: List<App> = listOf()
)

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

    init {
        viewModelScope.launch {
            userPreferencesRepository.clearPreferences()
        }
    }

    private val _uiState = MutableStateFlow(PreferencesUiState())
    val uiState: StateFlow<PreferencesUiState> = combine(
        userPreferencesRepository.getPreferredApps,
        _uiState
    ) { preferredApps, gestureUiState ->
        gestureUiState.copy(
            preferredApps = preferredApps
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PreferencesUiState()
    )

    fun getInstalledApps(context: Context): Deferred<List<App>> {
        return viewModelScope.async {
            val applicationInfoList: List<ApplicationInfo> = context
                .packageManager
                .getInstalledApplications(PackageManager.GET_META_DATA)

            val mutableListOfInstalledApps: MutableList<App> = mutableListOf()

            applicationInfoList.forEach { appInfo ->
                val packageName = appInfo.packageName
                val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
                if (launchIntent != null) {
                    mutableListOfInstalledApps.add(
                        App(
                            appName = context.packageManager.getApplicationLabel(appInfo).toString(),
                            packageName = packageName
                        )
                    )
                }
            }

            val listOfInstalledApps: List<App> = mutableListOfInstalledApps

            listOfInstalledApps
        }
    }

    fun setPreferredApp(indexOfGesture: Int, preferredApp: App) {
        viewModelScope.launch {
            userPreferencesRepository.setPreferredApp(
                indexOfGesture = indexOfGesture,
                preferredApp = preferredApp
            )
        }
    }
}
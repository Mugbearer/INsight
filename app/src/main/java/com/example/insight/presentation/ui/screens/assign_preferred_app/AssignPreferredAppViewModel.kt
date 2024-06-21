package com.example.insight.presentation.ui.screens.assign_preferred_app

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import com.example.insight.presentation.ui.shared.App
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

data class AssignPreferredAppUiState(
    val installedApps: List<App> = listOf(),
    val indexOfSelectedApp: Int? = null
)

class AssignPreferredAppViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AssignPreferredAppUiState())
    val uiState = _uiState.asStateFlow()

    private lateinit var textToSpeech: TextToSpeech

    fun setInstalledApps(installedApps: List<App>) {
        _uiState.update { currentState ->
            currentState.copy(
                installedApps = installedApps
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

    fun selectNextApp() {
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
    }

    fun selectPreviousApp() {
        val maxIndex: Int = uiState.value.installedApps.size - 1
        val newIndex = when (val oldIndex: Int? = uiState.value.indexOfSelectedApp) {
            null -> maxIndex
            0 -> maxIndex
            else -> oldIndex - 1
        }

        setIndexOfSelectedApp(newIndex)
    }

    fun initTextToSpeech(context: Context) {
        textToSpeech = TextToSpeech(
            context
        ) {
            if (it == TextToSpeech.SUCCESS) {
                textToSpeech.let { txtToSpeech ->
                    txtToSpeech.language = Locale.US
                    txtToSpeech.setSpeechRate(0.8f)
                    txtToSpeech.speak(
                        "Currently fetching your list of apps, please wait.",
                        TextToSpeech.QUEUE_ADD,
                        null,
                        null
                    )
                }
            }
        }
    }

    fun speakSelectedApp() {
        speakTextToSpeech(
            uiState.value.installedApps[uiState.value.indexOfSelectedApp!!].toString()
        )
    }

    fun speakTextToSpeech(message: String) {
        textToSpeech.speak(
            message,
            TextToSpeech.QUEUE_ADD,
            null,
            null
        )
    }

    fun stopTextToSpeech() {
        textToSpeech.stop()
    }

    fun shutdownTextToSpeech() {
        textToSpeech.shutdown()
    }
}
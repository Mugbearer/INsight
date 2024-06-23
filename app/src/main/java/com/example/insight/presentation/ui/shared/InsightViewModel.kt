package com.example.insight.presentation.ui.shared

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.insight.INsightApplication
import com.example.insight.data.UserPreferencesRepository
import com.example.insight.presentation.handlers.GestureHandler
import com.example.insight.presentation.handlers.IntentHandler
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

data class InsightUiState(
    val preferredApps: List<App> = listOf()
)

class InsightViewModel(
    application: Application,
    private val userPreferencesRepository: UserPreferencesRepository
) : AndroidViewModel(application) {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as INsightApplication
                InsightViewModel(application, application.userPreferencesRepository)
            }
        }
    }

    init {
        viewModelScope.launch {
            userPreferencesRepository.clearPreferences()
        }
    }

    private val gestureHandler: GestureHandler = GestureHandler()

    private val intentHandler: IntentHandler = IntentHandler()

    private val _uiState = MutableStateFlow(InsightUiState())
    val uiState: StateFlow<InsightUiState> = combine(
        userPreferencesRepository.getPreferredApps,
        _uiState
    ) { preferredApps, gestureUiState ->
        gestureUiState.copy(
            preferredApps = preferredApps
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InsightUiState()
    )

    private val appContext: Context = application.applicationContext

    private lateinit var textToSpeech: TextToSpeech

    private val audioManager: AudioManager = appContext
        .getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun getAppContext(): Context {
        return appContext
    }

    fun getInstalledApps(): Deferred<List<App>> {
        return viewModelScope.async {
            val applicationInfoList: List<ApplicationInfo> = appContext
                .packageManager
                .getInstalledApplications(PackageManager.GET_META_DATA)

            val mutableListOfInstalledApps: MutableList<App> = mutableListOf()

            applicationInfoList.forEach { appInfo ->
                val packageName = appInfo.packageName
                val launchIntent = appContext.packageManager.getLaunchIntentForPackage(packageName)
                if (launchIntent != null) {
                    mutableListOfInstalledApps.add(
                        App(
                            appName = appContext.packageManager.getApplicationLabel(appInfo).toString(),
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

    fun getIndexOfResult(bitmap: Bitmap): Deferred<Int> {
        return viewModelScope.async {
            gestureHandler.getIndexOfResult(
                context = appContext,
                bitmap = bitmap
            )
        }
    }

    fun getBitmapFromCanvas(canvasSize: Size, lines: List<Line>): Deferred<Bitmap> {
        return viewModelScope.async {
            drawBitmap(canvasSize, lines)
        }
    }

    private fun drawBitmap(canvasSize: Size, lines: List<Line>): Bitmap {
        val canvasWidthInt = canvasSize.width.toInt()
        val canvasHeightInt = canvasSize.height.toInt()
        val bitmap = Bitmap.createBitmap(canvasWidthInt, canvasHeightInt, Bitmap.Config.ARGB_8888)
        val drawScope = CanvasDrawScope()
        drawScope.draw(
            density = Density(context = appContext),
            layoutDirection = LayoutDirection.Ltr,
            canvas = androidx.compose.ui.graphics.Canvas(bitmap.asImageBitmap()),
            size = Size(canvasWidthInt.toFloat(), canvasHeightInt.toFloat())
        ) {
            drawRect(
                color = androidx.compose.ui.graphics.Color.White,
                size = size
            )

            lines.forEach { line ->
                drawLine(
                    color = line.color,
                    start = line.start,
                    end = line.end,
                    strokeWidth = line.strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        return bitmap
    }

    fun initTextToSpeech(message: String) {
        textToSpeech = TextToSpeech(
            appContext
        ) {
            if (it == TextToSpeech.SUCCESS) {
                textToSpeech.let { txtToSpeech ->
                    txtToSpeech.language = Locale.US
                    txtToSpeech.setSpeechRate(0.8f)
                    txtToSpeech.speak(
                        message,
                        TextToSpeech.QUEUE_ADD,
                        null,
                        null
                    )
                }
            }
        }
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

    fun getRingerMode(): Int {
        return audioManager.ringerMode
    }

    fun setRingerMode(ringerMode: Int) {
        audioManager.ringerMode = ringerMode
    }

    fun isExistsApp(indexOfApp: Int): Boolean {
        if (uiState.value.preferredApps[indexOfApp].packageName == "") return false

        val intent: Intent? = intentHandler
            .getPreferredAppIntent(
                context = appContext,
                appPackageName = uiState.value.preferredApps[indexOfApp].packageName
            )

        return intent != null
    }

    fun redirectToGoogle(context: Context) {
        context.startActivity(intentHandler.getGoogleIntent())
    }

    fun redirectToSettings(context: Context) {
        context.startActivity(intentHandler.getSettingsIntent())
    }

    fun redirectToKeypad(context: Context) {
        context.startActivity(intentHandler.getKeypadIntent())
    }

    fun redirectToPreferredApp(context: Context, indexOfApp: Int) {
        context.startActivity(
            intentHandler.getPreferredAppIntent(appContext,uiState.value.preferredApps[indexOfApp].packageName)!!
        )
    }

    fun redirectToContacts(context: Context) {
        context.startActivity(intentHandler.getGoogleIntent())
    }
}
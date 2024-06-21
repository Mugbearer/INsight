package com.example.insight.presentation.ui.screens.drawing

import android.content.Context
import android.graphics.Bitmap
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import androidx.annotation.VisibleForTesting
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.insight.domain.gesture.GestureHandler
import com.example.insight.presentation.ui.shared.Line
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

data class DrawingUiState(
    val canvasSize: Size = Size.Zero,
    val lines: MutableList<Line> = mutableListOf(),
    val isDrawing: Boolean = true,
    val isLoading: Boolean = false
)

class DrawingViewModel(
    private val gestureHandler: GestureHandler = GestureHandler()
) : ViewModel() {
    private val _uiState = MutableStateFlow(DrawingUiState())
    val uiState = _uiState.asStateFlow()

    private lateinit var textToSpeech: TextToSpeech

    private lateinit var audioManager: AudioManager

    fun onDraw(change: PointerInputChange, dragAmount: Offset) {
        val line: Line = getLine(change, dragAmount)
        addLine(line)
    }

    fun setCanvasSize(canvasSize: Size) {
        _uiState.update { currentState ->
            currentState.copy(
                canvasSize = canvasSize
            )
        }
    }

    @VisibleForTesting
    internal fun getLine(change: PointerInputChange, dragAmount: Offset): Line {
        return Line(
            start = change.position - dragAmount,
            end = change.position
        )
    }

    @VisibleForTesting
    internal fun addLine(line: Line) {
        _uiState.update { currentState ->
            currentState.copy(
                lines = currentState.lines.toMutableList().apply { add(line) }
            )
        }
    }

    fun emptyLines() {
        _uiState.update { currentState ->
            currentState.copy(
                lines = mutableListOf()
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

    fun setIsLoading(isLoading: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = isLoading
            )
        }
    }

    private fun drawBitmap(context: Context): Bitmap {
        val canvasWidthInt = uiState.value.canvasSize.width.toInt()
        val canvasHeightInt = uiState.value.canvasSize.height.toInt()
        val bitmap = Bitmap.createBitmap(canvasWidthInt, canvasHeightInt, Bitmap.Config.ARGB_8888)
        val drawScope = CanvasDrawScope()
        drawScope.draw(
            density = Density(context = context),
            layoutDirection = LayoutDirection.Ltr,
            canvas = androidx.compose.ui.graphics.Canvas(bitmap.asImageBitmap()),
            size = Size(canvasWidthInt.toFloat(), canvasHeightInt.toFloat())
        ) {
            drawRect(
                color = androidx.compose.ui.graphics.Color.White,
                size = size
            )

            uiState.value.lines.forEach { line ->
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

    fun getBitmapFromCanvas(context: Context): Deferred<Bitmap> {
        return viewModelScope.async {
            drawBitmap(context)
        }
    }

    fun getIndexOfResult(context: Context, bitmap: Bitmap): Deferred<Int> {
        return viewModelScope.async {
            gestureHandler.getIndexOfResult(
                context = context,
                bitmap = bitmap
            )
        }
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
                        "You are now in the gesture drawing screen. Please perform a gesture.",
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

    fun initAudioManager(context: Context) {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    fun setRingerMode(ringerMode: Int) {
        audioManager.ringerMode = ringerMode
    }
}
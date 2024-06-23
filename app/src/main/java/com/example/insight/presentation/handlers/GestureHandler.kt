package com.example.insight.presentation.handlers

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.example.insight.domain.gesture.GestureHandlerDomain
import com.example.insight.ml.GestureModel
import com.example.insight.presentation.ui.shared.Line
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

class GestureHandler {
    private val gestureHandlerDomain: GestureHandlerDomain = GestureHandlerDomain()

    suspend fun getIndexOfResult(context: Context, bitmap: Bitmap): Int {
        val newBitmap = gestureHandlerDomain.preprocessBitmap(bitmap)
        val byteBuffer: ByteBuffer = gestureHandlerDomain.getByteBufferFromBitmap(newBitmap)
        val floatArrayOfResults: FloatArray = getFloatArrayOfResults(context, byteBuffer)
        val findIndexOfMaxValue: Int = gestureHandlerDomain.getIndexOfMaxValue(floatArrayOfResults)

        return findIndexOfMaxValue
    }

    private suspend fun getFloatArrayOfResults(context: Context, byteBuffer: ByteBuffer): FloatArray {
        val model = GestureModel.newInstance(context)

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 28, 28, 1), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Releases model resources if no longer used.
        model.close()

        return outputFeature0.floatArray
    }

    suspend fun drawToBitmap(context: Context, canvasSize: Size, lines: List<Line>): Bitmap {
        val canvasWidthInt = canvasSize.width.toInt()
        val canvasHeightInt = canvasSize.height.toInt()
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
}
package com.example.insight.state.helperfunctions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.input.key.Key.Companion.F
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.example.insight.ml.GestureModel
import com.example.insight.state.Line
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.TransformToGrayscaleOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer


object GestureModelHelper {
    fun classifyImageAndGetProbabilities(context: Context, bitmap: Bitmap): FloatArray {
        val byteBuffer: ByteBuffer = convertBitmapToByteBuffer(
            bitmap = preprocessBitmap(bitmap)
        )

        //GestureModel is the final predictive model
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

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(28,28, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0.0f,255.0f))
            .add(TransformToGrayscaleOp())
            .build()

        tensorImage = imageProcessor.process(tensorImage)

        return tensorImage.buffer
    }

    fun findIndexOfMaxValue(array: FloatArray): Int {
        var maxIndex = 0

        array.forEachIndexed { index, fl ->
            if (array[maxIndex] < fl) {
                maxIndex = index
            }
        }

        if (array[maxIndex] < 0.6) {
            return 10
        }

        return maxIndex
    }

    fun drawToBitmap(context: Context, canvasSize: Size, lines: List<Line>): Bitmap {
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

    private fun preprocessBitmap(bitmap: Bitmap): Bitmap {

        return bitmap
            .trimBorders()
            .addWhiteSpace()
    }

    private fun Bitmap.trimBorders(): Bitmap {
        val borderColors = mutableListOf<Int>()

        // Collect border colors from the top and bottom rows
        for (x in 0 until width) {
            borderColors.add(getPixel(x, 0))
            borderColors.add(getPixel(x, height - 1))
        }

        // Collect border colors from the left and right columns (excluding corners)
        for (y in 1 until height - 1) {
            borderColors.add(getPixel(0, y))
            borderColors.add(getPixel(width - 1, y))
        }

        // Find the most frequent color in the border
        val mostFrequentColor = borderColors.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: Color.WHITE

        // Use the most frequent color to trim the borders
        var startX = 0
        loop@ for (x in 0 until width) {
            for (y in 0 until height) {
                if (getPixel(x, y) != mostFrequentColor) {
                    startX = x
                    break@loop
                }
            }
        }
        var startY = 0
        loop@ for (y in 0 until height) {
            for (x in 0 until width) {
                if (getPixel(x, y) != mostFrequentColor) {
                    startY = y
                    break@loop
                }
            }
        }
        var endX = width - 1
        loop@ for (x in endX downTo 0) {
            for (y in 0 until height) {
                if (getPixel(x, y) != mostFrequentColor) {
                    endX = x
                    break@loop
                }
            }
        }
        var endY = height - 1
        loop@ for (y in endY downTo 0) {
            for (x in 0 until width) {
                if (getPixel(x, y) != mostFrequentColor) {
                    endY = y
                    break@loop
                }
            }
        }
        val newWidth = endX - startX + 1
        val newHeight = endY - startY + 1

        return Bitmap.createBitmap(this, startX, startY, newWidth, newHeight)
    }

    private fun Bitmap.addWhiteSpace(): Bitmap {
        val squareSize = maxOf(width, height)
        val paddingSize = (squareSize * 0.1).toInt() // 10% of the square size

        // Calculate the size of the new bitmap
        val newSize = squareSize + 2 * paddingSize

        // Create a new bitmap with dimensions equal to the new size
        val resultBitmap = Bitmap.createBitmap(newSize, newSize, config)

        // Create a canvas with the result bitmap
        val canvas = android.graphics.Canvas(resultBitmap)

        // Fill the canvas with white color
        canvas.drawColor(Color.WHITE)

        // Calculate the starting point for drawing the original bitmap
        val startX = paddingSize + (squareSize - width) / 2
        val startY = paddingSize + (squareSize - height) / 2

        // Draw the original bitmap on the canvas
        canvas.drawBitmap(this, startX.toFloat(), startY.toFloat(), null)

        return resultBitmap
    }
}
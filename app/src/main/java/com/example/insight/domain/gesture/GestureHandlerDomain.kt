package com.example.insight.domain.gesture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.VisibleForTesting
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.example.insight.ml.GestureModel
import com.example.insight.presentation.ui.shared.Line
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.TransformToGrayscaleOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

class GestureHandlerDomain {
    fun getIndexOfMaxValue(array: FloatArray): Int {
        var maxIndex = 0

        array.forEachIndexed { index, fl ->
            if (array[maxIndex] < fl) {
                maxIndex = index
            }
        }

        if (array[maxIndex] < 0.7) {
            return 10 // returns 10 if confidence level is below 70%
        }

        return maxIndex
    }

    fun getByteBufferFromBitmap(bitmap: Bitmap): ByteBuffer {
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

    fun preprocessBitmap(bitmap: Bitmap): Bitmap {
        return bitmap
            .let { trimBorders(it) }
            .let { addWhiteSpace(it) }
    }

    @VisibleForTesting
    internal fun trimBorders(bitmap: Bitmap): Bitmap {
        val borderColors = mutableListOf<Int>()

        // Collect border colors from the top and bottom rows
        for (x in 0 until bitmap.width) {
            borderColors.add(bitmap.getPixel(x, 0))
            borderColors.add(bitmap.getPixel(x, bitmap.height - 1))
        }

        // Collect border colors from the left and right columns (excluding corners)
        for (y in 1 until bitmap.height - 1) {
            borderColors.add(bitmap.getPixel(0, y))
            borderColors.add(bitmap.getPixel(bitmap.width - 1, y))
        }

        // Find the most frequent color in the border
        val mostFrequentColor = borderColors.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: Color.WHITE

        // Use the most frequent color to trim the borders
        var startX = 0
        loop@ for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                if (bitmap.getPixel(x, y) != mostFrequentColor) {
                    startX = x
                    break@loop
                }
            }
        }
        var startY = 0
        loop@ for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                if (bitmap.getPixel(x, y) != mostFrequentColor) {
                    startY = y
                    break@loop
                }
            }
        }
        var endX = bitmap.width - 1
        loop@ for (x in endX downTo 0) {
            for (y in 0 until bitmap.height) {
                if (bitmap.getPixel(x, y) != mostFrequentColor) {
                    endX = x
                    break@loop
                }
            }
        }
        var endY = bitmap.height - 1
        loop@ for (y in endY downTo 0) {
            for (x in 0 until bitmap.width) {
                if (bitmap.getPixel(x, y) != mostFrequentColor) {
                    endY = y
                    break@loop
                }
            }
        }
        val newWidth = endX - startX + 1
        val newHeight = endY - startY + 1

        return Bitmap.createBitmap(bitmap, startX, startY, newWidth, newHeight)
    }

    @VisibleForTesting
    internal fun addWhiteSpace(bitmap: Bitmap): Bitmap {
        val squareSize = maxOf(bitmap.width, bitmap.height)
        val paddingSize = (squareSize * 0.1).toInt() // 10% of the square size

        // Calculate the size of the new bitmap
        val newSize = squareSize + 2 * paddingSize

        // Create a new bitmap with dimensions equal to the new size
        val resultBitmap = Bitmap.createBitmap(newSize, newSize, bitmap.config)

        // Create a canvas with the result bitmap
        val canvas = android.graphics.Canvas(resultBitmap)

        // Fill the canvas with white color
        canvas.drawColor(Color.WHITE)

        // Calculate the starting point for drawing the original bitmap
        val startX = paddingSize + (squareSize - bitmap.width) / 2
        val startY = paddingSize + (squareSize - bitmap.height) / 2

        // Draw the original bitmap on the canvas
        canvas.drawBitmap(bitmap, startX.toFloat(), startY.toFloat(), null)

        return resultBitmap
    }
}
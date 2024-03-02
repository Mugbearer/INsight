package com.example.insight.state.helperfunctions

import android.content.Context
import android.graphics.Bitmap
import com.example.insight.ml.Yolov8mFloat32
import org.tensorflow.lite.support.image.TensorImage

object EnvironmentSensingHelper {
    fun getEnvironmentSensingOutput(context: Context, bitmap: Bitmap) {
        val model = Yolov8mFloat32.newInstance(context)

        //Creates inputs for reference.
        val image = TensorImage.fromBitmap(bitmap)

        //Runs model inference and gets result.
        val outputs = model.process(image)
        val output = outputs.outputAsCategoryList

        //Releases model resources if no longer used.
        model.close()
    }
}

fun Bitmap.resize(maxWidth: Int, maxHeight: Int): Bitmap {
    if (maxHeight > 0 && maxWidth > 0) {
        val width = this.width
        val height = this.height
        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

        var finalWidth = maxWidth
        var finalHeight = maxHeight
        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }
        return Bitmap.createScaledBitmap(this, finalWidth, finalHeight, true)
    } else {
        return this
    }
}

package com.example.insight.state.helperfunctions

import android.content.Context
import android.graphics.Bitmap
import com.example.insight.yolo.Recognition
import com.example.insight.yolo.Yolov5TFLiteDetector
import java.lang.reflect.Array

object EnvironmentSensingHelper {
    fun getEnvironmentSensingOutput(context: Context, bitmap: Bitmap): MutableList<String> {
        var yolov5TFLiteDetector = Yolov5TFLiteDetector()
        yolov5TFLiteDetector.setModelFile("yolov5s-fp16.tflite")
        yolov5TFLiteDetector.initialModel(context)
        var recognitions: ArrayList<Recognition?> =  yolov5TFLiteDetector.detect(bitmap)

        val results = mutableListOf<String>()

        recognitions.forEach{
            if (it != null) {
                if(it.confidence!! > 0.4) {
                    results.add(it.labelName!!)
                }
            }
        }

        return results
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

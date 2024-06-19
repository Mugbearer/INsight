package com.example.insight.domain.environment_sensing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log

class EnvironmentSensingHandler {
    fun getEnvironmentSensingResult(context: Context, bitmap: Bitmap): String {
        val yolov5TFLiteDetector = Yolov5TFLiteDetector()
        yolov5TFLiteDetector.setModelFile("yolov5s-fp16.tflite")
        yolov5TFLiteDetector.initialModel(context)

        return formatHashMapResultAndGetString(
            getNestedHashMapOfResults(
                yolov5TFLiteDetector.detect(bitmap)
            )
        )
    }

    private fun getNestedHashMapOfResults(
        recognitions: ArrayList<Recognition?>
    ): HashMap<String, HashMap<String, Int>> {
        val nestedHashMap: HashMap<String, HashMap<String, Int>> = hashMapOf()
        nestedHashMap["Left"] = hashMapOf()
        nestedHashMap["Middle"] = hashMapOf()
        nestedHashMap["Right"] = hashMapOf()


        Log.d("recognition","before")
        recognitions.forEach{
            if (it != null) {
                if (it.confidence!! > 0.4) {
                    Log.d(
                        "recognition",
                        "${it.labelName} ${
                            it.getLocation().left
                        } ${
                            getMiddleHorizontal(it.getLocation())
                        } ${
                            it.getLocation().right
                        } ${
                            it.getLocation().top
                        } ${
                            it.getLocation().bottom
                        }"
                    )

                    val label = it.labelName!!
                    val middlePoint = getMiddleHorizontal(it.getLocation())

                    val position = if (middlePoint <= 50) {
                        "Left"
                    }
                    else if (middlePoint < 176) {
                        "Middle"
                    }
                    else {
                        "Right"
                    }

                    if(!nestedHashMap[position]!!.contains(label)) {
                        nestedHashMap[position]!![label] = 1
                    }
                    else {
                        nestedHashMap[position]!![label] = nestedHashMap[position]!![label]!! + 1
                    }
                }
            }
        }

        Log.d("recognition","after")

        nestedHashMap.forEach { (key, hashMap) ->
            Log.d("recognition", key)
            hashMap.forEach{
                Log.d("recognition",it.value.toString()+it.key)
            }
        }

        return nestedHashMap
    }

    private fun formatHashMapResultAndGetString(
        nestedHashMap: HashMap<String, HashMap<String, Int>>
    ): String {
        val results: MutableList<String> = mutableListOf()

        nestedHashMap.forEach{ (position, labelAndNumbers) ->
            if (labelAndNumbers.isEmpty()) return@forEach
            var result = "$position has "
            val labelAndNumberStrings: MutableList<String> = mutableListOf()

            labelAndNumbers.forEach{ (label, number) ->
                labelAndNumberStrings.add(
                    "$number $label"
                )
            }

            result += labelAndNumberStrings.joinToString(", ")

            results.add(result)
        }

        Log.d("recognition", results.joinToString(". "))

        return results.joinToString(". ")
    }

    private fun getMiddleHorizontal(rect: RectF): Float {
        return rect.left + (rect.right - rect.left) / 2
    }
}
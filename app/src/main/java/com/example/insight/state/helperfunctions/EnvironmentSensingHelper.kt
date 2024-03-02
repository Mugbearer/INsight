package com.example.insight.state.helperfunctions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import com.example.insight.yolo.Recognition
import com.example.insight.yolo.Yolov5TFLiteDetector

object EnvironmentSensingHelper {
    fun getEnvironmentSensingOutput(context: Context, bitmap: Bitmap): String {
        var yolov5TFLiteDetector = Yolov5TFLiteDetector()
        yolov5TFLiteDetector.setModelFile("yolov5s-fp16.tflite")
        yolov5TFLiteDetector.initialModel(context)
//        var recognitions: ArrayList<Recognition?> =  yolov5TFLiteDetector.detect(bitmap)

//        val results = mutableListOf<String>()
//
//        recognitions.forEach{
//            if (it != null) {
//                if(it.confidence!! > 0.4) {
//                    results.add(it.labelName!!)
//                }
//            }
//        }
//
//        return results

        return getResults(
            createNestedHashMapOfResults(
                yolov5TFLiteDetector.detect(bitmap)
            )
        )

//        createNestedHashMapOfResults(
//            yolov5TFLiteDetector.detect(bitmap)
//        )

//        return "balisa 'lang humpay sa paghuhukay ng mga tula"
    }

    private fun createNestedHashMapOfResults(
        recognitions: ArrayList<Recognition?>
    ): HashMap<String, HashMap<String, Int>> {
        var nestedHashMap: HashMap<String, HashMap<String, Int>> = hashMapOf()
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

    private fun getResults(
        nestedHashMap: HashMap<String, HashMap<String, Int>>
    ): String {
        var results: MutableList<String> = mutableListOf()

        nestedHashMap.forEach{ (position, labelAndNumbers) ->
            if (labelAndNumbers.isEmpty()) return@forEach
            var result = "$position has "
            var labelAndNumberStrings: MutableList<String> = mutableListOf()

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

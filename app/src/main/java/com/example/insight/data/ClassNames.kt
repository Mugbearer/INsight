package com.example.insight.data

object ClassNames {
    fun list(): Array<String> {
        return arrayOf(
            "circle", //0: google.com (browser)
            "square", //1: environment sensing
            "triangle", //2: settings
            "x", //3: keypad
            "double-line-horizontal", //4: assignable app one
            "double-line-vertical", //5: silent
            "triple-line-horizontal", //6: contacts
            "triple-line-vertical", //7: assignable app two
            "diagonal-from-right", //8: assignable app three (backward slash)
            "diagonal-from-left" //9: reassign app (forward slash)
        )
    }
}
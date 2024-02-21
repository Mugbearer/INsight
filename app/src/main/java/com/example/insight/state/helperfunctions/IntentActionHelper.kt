package com.example.insight.state.helperfunctions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore

object IntentActionHelper {
    fun launchPhoneInterface(context: Context) {
        val intent = Intent(Intent.ACTION_DIAL)
        context.startActivity(intent)
    }

    fun launchEmailApp(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
        }
        context.startActivity(intent)
    }

    fun launchBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    }

    fun launchCamera(context: Context) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        context.startActivity(intent)
    }
}
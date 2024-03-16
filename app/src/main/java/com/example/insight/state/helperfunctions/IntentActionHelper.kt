package com.example.insight.state.helperfunctions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Settings


object IntentActionHelper {
    fun launchPhoneInterface(context: Context) {
        val intent = Intent(Intent.ACTION_DIAL)
        context.startActivity(intent)
    }

    fun launchBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    }

    fun launchPreferredAppIntent(context: Context, packageName: String): Intent? {
        return context.packageManager.getLaunchIntentForPackage(packageName)
    }

    fun launchSettings(context: Context) {
        val intent = Intent(Settings.ACTION_SETTINGS)
        context.startActivity(intent)
    }

    fun launchContacts(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.type = ContactsContract.Contacts.CONTENT_TYPE
        context.startActivity(intent)
    }
}
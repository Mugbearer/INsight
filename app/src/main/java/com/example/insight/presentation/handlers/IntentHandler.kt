package com.example.insight.presentation.handlers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Settings

class IntentHandler() {
    fun getGoogleIntent(): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://google.com")
        return intent
    }

    fun getSettingsIntent(): Intent {
        return Intent(Settings.ACTION_SETTINGS)
    }

    fun getKeypadIntent(): Intent {
        return Intent(Intent.ACTION_DIAL)
    }

    fun getPreferredAppIntent(context: Context, appPackageName: String): Intent? {
        return context
            .packageManager
            .getLaunchIntentForPackage(appPackageName)
    }

    fun getContactsIntent(): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.type = ContactsContract.Contacts.CONTENT_TYPE
        return intent
    }
}
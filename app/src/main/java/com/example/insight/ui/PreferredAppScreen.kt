package com.example.insight.ui

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun PreferredAppScreen(
    modifier: Modifier = Modifier,
    getHashMapOfApps: (Context) -> Map<String, String>,
    setPreferredApp: (String) -> Unit
) {
    val context = LocalContext.current
    val apps: Map<String, String> = getHashMapOfApps(context)

    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(apps.toList()) { _, (appName, packageName) ->
            Button(
                onClick = {
                    setPreferredApp(packageName)
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text(text = appName)
            }
        }
    }
}
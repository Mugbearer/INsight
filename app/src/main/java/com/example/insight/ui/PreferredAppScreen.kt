package com.example.insight.ui

import android.content.Context
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insight.data.App
import com.example.insight.state.helperfunctions.useTts

@Composable
fun PreferredAppScreen(
    modifier: Modifier = Modifier,
    indexOfGesture: Int,
    listOfInstalledApps: List<App>,
    selectedApp: App?,
    getSelectedApp: () -> App?,
    navigateToNextButtonAndReturnAppName: () -> String,
    navigateToPreviousButtonAndReturnAppName: () -> String,
    setPreferredApp: (Int) -> Unit
) {
    val context: Context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        context.useTts(navigateToNextButtonAndReturnAppName())
                    },
                    onDoubleTap = {
                        context.useTts(navigateToPreviousButtonAndReturnAppName())
                    },
                    onLongPress = {
                        val app: App? = getSelectedApp()
                        if (app != null) {
                            context.useTts("Assigning ${app.appName}")
                            setPreferredApp(indexOfGesture)
                        }
                        else {
                            context.useTts("Please select an app")
                        }
                    }
                )
            },
    ) {
        items(listOfInstalledApps) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = it.appName,
                    style = TextStyle(fontSize = 16.sp),
                    color = if (
                        selectedApp != null &&
                        it.appName == selectedApp.appName
                    ) {
                        Color.Green
                    }
                    else Color.Black
                )
                Divider(color = Color.Black, thickness = 1.dp)
            }
        }
    }
}
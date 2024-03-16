package com.example.insight.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.insight.state.helperfunctions.useTts

@Composable
fun PreferredAppScreen(
    modifier: Modifier = Modifier,
    listOfInstalledApps: List<String>,
    selectedApp: String?,
    navigateToNextButton: () -> String,
    navigateToPreviousButton: () -> String,
    setPreferredApp: (Context) -> Unit
) {
    Log.d("tag", listOfInstalledApps.size.toString())
    val context: Context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        Log.d("tap","onTap")
                        context.useTts(navigateToNextButton())
                    },
                    onDoubleTap = {
                        Log.d("tap","onDoubleTap")
                        context.useTts(navigateToPreviousButton())
                    },
                    onLongPress = {
                        Log.d("tap","onLongPress")
                        setPreferredApp(context)
                    }
                )
            },
//        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(listOfInstalledApps) {
//            Button(
//                onClick = {},
//                enabled = false,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                colors = if (
//                    indexOfSelectedApp != null &&
//                    it == listOfInstalledApps[indexOfSelectedApp]
//                ) {
//                    ButtonDefaults.buttonColors(
//                        containerColor = Color.Red
//                    )
//                }
//                else {
//                    ButtonDefaults.buttonColors()
//                }
//            ) {
//                Text(text = it)
//            }

//            Text(
//                text = it,
//                color = if (
//                    selectedApp != null &&
//                    it == selectedApp
//                ) {
//                    Color.Green
//                }
//                else Color.Black
//            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = it,
                    style = TextStyle(fontSize = 16.sp),
                    color = if (
                        selectedApp != null &&
                        it == selectedApp
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
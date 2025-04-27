package com.example.sit305_61d.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import android.util.Log

@Composable
fun DebugScreen() {
    Log.d("DebugScreen", "DebugScreen composable started.")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Cyan), // Different obvious color
        contentAlignment = Alignment.Center
    ) {
        Text("Debug Screen Reached!")
    }
}

package com.sahata

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SettingScreen(
    backgroundResId: Int,
    fieldResId: Int
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Background image
        Image(
            painter = painterResource(id = backgroundResId),
            contentDescription = "Setting Background",
            modifier = Modifier.fillMaxSize()
        )

        // Centered field
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = fieldResId),
                contentDescription = "Setting Field",
                modifier = Modifier.size(340.dp) // Adjust size as needed
            )
        }
    }
}
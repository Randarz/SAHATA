package com.sahata

import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun BelajarScreen(
    belajarBackgroundResId: Int,
    belajarNoticeResId: Int,
    inangBackgroundLayoutResId: Int,
    inangBackResId: Int,
    inangNextResId: Int,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    var showNotice by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (showNotice) {
            // Belajar notice screen
            Image(
                painter = painterResource(id = belajarBackgroundResId),
                contentDescription = "Belajar Background",
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = belajarNoticeResId),
                    contentDescription = "Belajar Notice",
                    modifier = Modifier
                        .size(500.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { showNotice = false }
                )
            }
        } else {
            // Inang screen using XML layout
            AndroidView(
                factory = { context ->
                    LayoutInflater.from(context).inflate(inangBackgroundLayoutResId, null)
                },
                modifier = Modifier.fillMaxSize()
            )

            // Back button
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Image(
                    painter = painterResource(id = inangBackResId),
                    contentDescription = "Inang Back",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onBackClick() }
                )
            }

            // Next button
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Image(
                    painter = painterResource(id = inangNextResId),
                    contentDescription = "Inang Next",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onNextClick() }
                )
            }
        }
    }
}
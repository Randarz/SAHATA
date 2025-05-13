package com.sahata

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun HomeScreen(
    backgroundResId: Int,
    belajarResId: Int,
    bermainResId: Int,
    settingResId: Int,
    noticeResId: Int,
    onSettingClick: () -> Unit,
    onNoticeClick: () -> Unit,
    onBelajarClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Background image
        Image(
            painter = painterResource(id = backgroundResId),
            contentDescription = "Home Background",
            modifier = Modifier.fillMaxSize()
        )

        // Centered belajar button with offset
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = belajarResId),
                contentDescription = "Belajar Button",
                modifier = Modifier
                    .size(200.dp)
                    .offset(y = 20.dp)
                    .clickable(
                        indication = null, // Remove ripple effect
                        interactionSource = remember { MutableInteractionSource() } // Disable default interaction
                    ) { onBelajarClick() }
            )
        }

        // Bermain button offset from belajar
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = bermainResId),
                contentDescription = "Bermain Button",
                modifier = Modifier
                    .size(200.dp)
                    .offset(y = 100.dp)
            )
        }

        // Top-right setting button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Image(
                painter = painterResource(id = settingResId),
                contentDescription = "Setting Button",
                modifier = Modifier
                    .size(50.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onSettingClick() }
            )
        }

        // Bottom-right notice button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Image(
                painter = painterResource(id = noticeResId),
                contentDescription = "Notice Button",
                modifier = Modifier
                    .size(50.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onNoticeClick() }
            )
        }
    }
}
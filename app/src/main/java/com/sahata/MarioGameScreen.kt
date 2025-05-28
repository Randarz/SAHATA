package com.sahata

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.ui.input.pointer.pointerInput

// Custom Rect class to define platform boundaries
data class Rect(val left: Float, val top: Float, val right: Float, val bottom: Float)

@Composable
fun MarioGameScreen(onBackClick: () -> Unit) {
    var playerX by remember { mutableStateOf(100f) }
    var playerY by remember { mutableStateOf(300f) }
    val velocityY = remember { mutableStateOf(0f) }
    val gravity = 0.5f
    val jumpForce = -10f
    var isJumping by remember { mutableStateOf(false) }
    var isMovingLeft by remember { mutableStateOf(false) }
    var isMovingRight by remember { mutableStateOf(false) }

    // Define platforms
    val platforms = listOf(
        Rect(50f, 350f, 250f, 370f), // Platform 1
        Rect(300f, 250f, 450f, 270f) // Platform 2
    )

    LaunchedEffect(Unit) {
        while (true) {
            // Apply gravity
            velocityY.value += gravity
            playerY += velocityY.value

            // Check if the player is on a platform
            val playerBottom = playerY + 50f // Player's bottom edge
            val playerCenterX = playerX + 25f // Player's horizontal center
            var onPlatform = false

            for (platform in platforms) {
                if (playerBottom in platform.top..platform.bottom &&
                    playerCenterX in platform.left..platform.right
                ) {
                    playerY = platform.top - 50f // Place player on top of the platform
                    velocityY.value = 0f
                    isJumping = false
                    onPlatform = true
                    break
                }
            }

            // Prevent falling below ground if not on a platform
            if (!onPlatform && playerY > 300f) {
                playerY = 300f
                velocityY.value = 0f
                isJumping = false
            }

            // Handle continuous movement
            if (isMovingLeft) playerX -= 5f
            if (isMovingRight) playerX += 5f

            // Redraw every 16ms (~60 FPS)
            delay(16L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Cyan) // Placeholder for sky color
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.bermain_background), // Replace with your background asset
            contentDescription = "Game Background",
            modifier = Modifier.fillMaxSize()
        )

        // Draw platforms
        platforms.forEach { platform ->
            Box(
                modifier = Modifier
                    .size(
                        width = (platform.right - platform.left).dp,
                        height = (platform.bottom - platform.top).dp
                    )
                    .offset(x = platform.left.dp, y = platform.top.dp)
                    .background(Color.Green) // Placeholder for platform
            )
        }

        // Player character (Mario)
        Image(
            painter = painterResource(id = R.drawable.autoplay), // Replace with your Mario sprite asset
            contentDescription = "Mario",
            modifier = Modifier
                .size(50.dp)
                .offset(x = playerX.dp, y = playerY.dp)
        )

        // Left Button
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(50.dp)
                .align(Alignment.BottomStart)
                .offset(x = 0.dp, y = (-16).dp) // Adjust position for alignment
                .background(Color.Gray)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isMovingLeft = true
                            tryAwaitRelease()
                            isMovingLeft = false
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.inang_back), // Replace with your left arrow asset
                contentDescription = "Left Button"
            )
        }

        // Right Button
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(50.dp)
                .align(Alignment.BottomStart)
                .offset(x = 60.dp, y = (-16).dp) // Place next to the left button
                .background(Color.Gray)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isMovingRight = true
                            tryAwaitRelease()
                            isMovingRight = false
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.inang_next), // Replace with your right arrow asset
                contentDescription = "Right Button"
            )
        }

        // Jump Button
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(50.dp)
                .align(Alignment.BottomEnd)
                .background(Color.Gray)
                .clickable {
                    if (!isJumping) {
                        velocityY.value = jumpForce // Jump
                        isJumping = true
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.anak_next), // Replace with your jump button asset
                contentDescription = "Jump Button"
            )
        }

        // Back button
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(50.dp)
                .background(Color.Gray)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.setting_kembali), // Replace with your back button asset
                contentDescription = "Back Button"
            )
        }
    }
}
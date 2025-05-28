package com.sahata

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.layout.ContentScale


@Composable
fun MariBermainScreen(
    mariBackgroundLayoutResId: Int,
    homeButtonResId: Int,
    onHomeClick: () -> Unit,
    onPetualanganClick: () -> Unit,
    onTebakAksaraClick: () -> Unit
) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.button) }
    var showPopup by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ComposeColor.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                val view = LayoutInflater.from(ctx).inflate(mariBackgroundLayoutResId, null)

                val petualanganButton = view.findViewById<ImageView>(R.id.bermain_petualangan)
                val tebakAksaraButton = view.findViewById<ImageView>(R.id.bermain_tebak)

                petualanganButton.setOnClickListener {
                    mediaPlayer.start()
                    showPopup = true
                }

                tebakAksaraButton.setOnClickListener {
                    mediaPlayer.start()
                    onTebakAksaraClick()
                }

                view
            },
            modifier = Modifier.fillMaxSize()
        )

        // Home Button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Image(
                painter = painterResource(id = homeButtonResId),
                contentDescription = "Home Button",
                modifier = Modifier
                    .size(50.dp)
                    .clickable(
                        indication = null,
                        interactionSource = MutableInteractionSource()
                    ) {
                        mediaPlayer.start()
                        onHomeClick()
                    }
            )
        }

        // Fullscreen Image Popup
        if (showPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ComposeColor.Black.copy(alpha = 0.85f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        showPopup = false
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sebelum_buttonpetualangan),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            showPopup = false
                        }
                )
            }
        }
    }
}

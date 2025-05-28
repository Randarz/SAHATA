package com.sahata

import android.media.MediaPlayer
import android.view.View
import android.widget.ImageView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.sahata.R

@Composable
fun NoticeScreen(
    onBackToHome: () -> Unit,
    soundEffectsVolume: Int
) {
    val context = LocalContext.current

    var currentVolume by remember { mutableStateOf(soundEffectsVolume) }
    LaunchedEffect(soundEffectsVolume) {
        currentVolume = soundEffectsVolume
    }

    fun playSound(resId: Int) {
        val mediaPlayer = MediaPlayer.create(context, resId)
        mediaPlayer.setVolume(currentVolume / 5f, currentVolume / 5f)
        mediaPlayer.setOnCompletionListener { it.release() }
        mediaPlayer.start()
    }

    AndroidView(
        factory = { ctx ->
            val view = View.inflate(ctx, R.layout.activity_notice, null)
            val backButton = view.findViewById<ImageView>(R.id.notice_kembali)

            backButton.setOnClickListener {
                playSound(R.raw.button)
                onBackToHome()
            }

            view
        },
        modifier = Modifier.fillMaxSize()
    )
}

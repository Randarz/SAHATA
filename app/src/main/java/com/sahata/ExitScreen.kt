package com.sahata

import android.app.Activity
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
fun ExitScreen(
    onBackToSetting: () -> Unit,
    soundEffectsVolume: Float
) {
    val context = LocalContext.current

    fun playSoundEffect(resId: Int) {
        val player = MediaPlayer.create(context, resId)
        player.setVolume(soundEffectsVolume, soundEffectsVolume)
        player.setOnCompletionListener { it.release() }
        player.start()
    }

    AndroidView(
        factory = { ctx ->
            val view = View.inflate(ctx, R.layout.activity_exit, null)

            val kembaliButton = view.findViewById<ImageView>(R.id.exit_kembali)
            val keluarButton = view.findViewById<ImageView>(R.id.exit_keluar)

            kembaliButton.setOnClickListener {
                playSoundEffect(R.raw.button)
                onBackToSetting()
            }

            keluarButton.setOnClickListener {
                playSoundEffect(R.raw.button)
                (ctx as? Activity)?.finishAffinity()
            }

            view
        },
        modifier = Modifier.fillMaxSize()
    )
}

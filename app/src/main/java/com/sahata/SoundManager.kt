package com.sahata

import android.content.Context
import android.media.MediaPlayer

class SoundManager(private val context: Context) {
    private var soundEffectVolume: Float = 1.0f // Default volume (100%)

    fun setVolume(level: Int) {
        soundEffectVolume = (level.coerceIn(0, 5) * 0.2f) // Map 0-5 to 0.0-1.0
    }

    fun playSoundEffect(soundResId: Int) {
        val mediaPlayer = MediaPlayer.create(context, soundResId)
        mediaPlayer.setVolume(soundEffectVolume, soundEffectVolume)
        mediaPlayer.setOnCompletionListener { it.release() }
        mediaPlayer.start()
    }
}
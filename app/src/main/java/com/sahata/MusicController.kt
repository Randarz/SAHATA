package com.sahata

import android.media.MediaPlayer

object MusicController {
    var mediaPlayer: MediaPlayer? = null

    fun startMusic() {
        mediaPlayer?.start()
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
    }

    fun updateMusicVolume(level: Int) {
        val volume = level.coerceIn(0, 5) * 0.2f
        mediaPlayer?.setVolume(volume, volume)
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

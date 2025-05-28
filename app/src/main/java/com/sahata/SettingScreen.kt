package com.sahata

import android.content.Context
import android.media.MediaPlayer
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.fillMaxSize
import com.sahata.R

@Composable
fun SettingScreen(
    getMusicVolumeLevel: () -> Int,
    setMusicVolumeLevel: (Int) -> Unit,
    getSuaraVolumeLevel: () -> Int,
    setSuaraVolumeLevel: (Int) -> Unit,
    soundEffectsVolume: Int,
    onBackToHome: () -> Unit,
    onNavigateToExit: () -> Unit
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
        factory = {
            val view = View.inflate(it, R.layout.activity_setting, null)

            val musicBar = view.findViewById<SeekBar>(R.id.setting_barmusik)
            val musicDown = view.findViewById<ImageView>(R.id.setting_musikmin)
            val musicUp = view.findViewById<ImageView>(R.id.setting_musikplus)

            val suaraBar = view.findViewById<SeekBar>(R.id.setting_barsuara)
            val suaraDown = view.findViewById<ImageView>(R.id.setting_suaramin)
            val suaraUp = view.findViewById<ImageView>(R.id.setting_suaraplus)

            musicBar.max = 5
            musicBar.progress = getMusicVolumeLevel()
            musicBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        setMusicVolumeLevel(progress)
                        // No sound here
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            musicDown.setOnClickListener {
                if (getMusicVolumeLevel() > 0) {
                    setMusicVolumeLevel(getMusicVolumeLevel() - 1)
                    musicBar.progress = getMusicVolumeLevel()
                    playSound(R.raw.down)
                }
            }

            musicUp.setOnClickListener {
                if (getMusicVolumeLevel() < 5) {
                    setMusicVolumeLevel(getMusicVolumeLevel() + 1)
                    musicBar.progress = getMusicVolumeLevel()
                    playSound(R.raw.up)
                }
            }

            suaraBar.max = 5
            suaraBar.progress = getSuaraVolumeLevel()
            suaraBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        setSuaraVolumeLevel(progress)
                        // No sound here
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            suaraDown.setOnClickListener {
                if (getSuaraVolumeLevel() > 0) {
                    setSuaraVolumeLevel(getSuaraVolumeLevel() - 1)
                    suaraBar.progress = getSuaraVolumeLevel()
                    playSound(R.raw.down)
                }
            }

            suaraUp.setOnClickListener {
                if (getSuaraVolumeLevel() < 5) {
                    setSuaraVolumeLevel(getSuaraVolumeLevel() + 1)
                    suaraBar.progress = getSuaraVolumeLevel()
                    playSound(R.raw.up)
                }
            }

            val kembaliButton = view.findViewById<ImageView>(R.id.setting_kembali)
            kembaliButton.setOnClickListener {
                playSound(R.raw.button)
                onBackToHome()
            }

            val keluarButton = view.findViewById<ImageView>(R.id.setting_keluar)
            keluarButton.setOnClickListener {
                playSound(R.raw.button)
                onNavigateToExit()
            }

            view
        },
        modifier = Modifier.fillMaxSize()
    )
}
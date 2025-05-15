package com.sahata

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

class SettingScreen : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)
        val savedVolume = sharedPreferences.getInt("system_volume", audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM))

        // Set the saved volume
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, savedVolume, 0)

        val seekBar = findViewById<SeekBar>(R.id.setting_barsuara)
        val volumeDown = findViewById<ImageView>(R.id.setting_suaramin)
        val volumeUp = findViewById<ImageView>(R.id.setting_suaraplus)

        // Set SeekBar max and current progress
        seekBar.max = maxVolume
        seekBar.progress = savedVolume

        // Handle SeekBar changes
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progress, 0)
                    saveVolume(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Handle volume down button
        volumeDown.setOnClickListener {
            val newVolume = (audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) - 1).coerceAtLeast(0)
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, newVolume, 0)
            seekBar.progress = newVolume
            saveVolume(newVolume)
        }

        // Handle volume up button
        volumeUp.setOnClickListener {
            val newVolume = (audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) + 1).coerceAtMost(maxVolume)
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, newVolume, 0)
            seekBar.progress = newVolume
            saveVolume(newVolume)
        }
    }

    private fun saveVolume(volume: Int) {
        sharedPreferences.edit().putInt("system_volume", volume).apply()
    }
}
package com.sahata

import android.content.Context

object VolumePreferences {
    private const val PREF_NAME = "volume_prefs"
    private const val KEY_MUSIC_VOLUME = "music_volume"
    private const val KEY_SOUND_EFFECT_VOLUME = "sound_effect_volume"

    fun saveMusicVolume(context: Context, volume: Float) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putFloat(KEY_MUSIC_VOLUME, volume).apply()
    }

    fun saveSoundEffectVolume(context: Context, volume: Float) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putFloat(KEY_SOUND_EFFECT_VOLUME, volume).apply()
    }

    fun getMusicVolume(context: Context): Float {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(KEY_MUSIC_VOLUME, 1.0f)
    }

    fun getSoundEffectVolume(context: Context): Float {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(KEY_SOUND_EFFECT_VOLUME, 1.0f)
    }
}

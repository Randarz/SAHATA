package com.sahata

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.media.MediaPlayer
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlinx.coroutines.*

fun stopAutoplay(coroutineScope: CoroutineScope) {
    coroutineScope.coroutineContext.cancelChildren()
}

fun playSoundWithAnimation(context: Context, soundResId: Int, volume: Float, button: View) {
    val mediaPlayer = MediaPlayer.create(context, soundResId)
    mediaPlayer.setVolume(volume, volume)
    mediaPlayer.setOnCompletionListener { it.release() }
    mediaPlayer.start()
    animateButton(button, mediaPlayer.duration.toLong())
}

fun startAutoplay(
    coroutineScope: CoroutineScope,
    soundMap: Map<Int, Int>,
    context: Context,
    view: View,
    volume: Float
) {
    coroutineScope.launch {
        for ((imageViewId, soundResId) in soundMap) {
            if (!isActive) break
            val button = view.findViewById<View>(imageViewId)
            playSoundSequentiallyWithAnimation(context, soundResId, volume, button)
        }
    }
}

private suspend fun playSoundSequentiallyWithAnimation(
    context: Context,
    soundResId: Int,
    volume: Float,
    button: View
) {
    val mediaPlayer = MediaPlayer.create(context, soundResId)
    try {
        mediaPlayer.setVolume(volume, volume)
        mediaPlayer.start()
        animateButton(button, mediaPlayer.duration.toLong())
        delay(mediaPlayer.duration.toLong())
    } finally {
        mediaPlayer.release()
    }
}

fun animateButton(view: View, duration: Long) {
    val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f, 1f)
    val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f, 1f)
    scaleX.duration = duration
    scaleY.duration = duration
    val animatorSet = AnimatorSet()
    animatorSet.playTogether(scaleX, scaleY)
    animatorSet.interpolator = AccelerateDecelerateInterpolator()
    animatorSet.start()
}

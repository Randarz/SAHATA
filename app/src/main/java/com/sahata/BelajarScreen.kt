package com.sahata

import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.*

@Composable
fun BelajarScreen(
    belajarBackgroundResId: Int,
    belajarNoticeResId: Int,
    inangBackgroundLayoutResId: Int,
    inangBackResId: Int,
    inangNextResId: Int,
    soundEffectsVolume: Float,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val currentVolume by rememberUpdatedState(soundEffectsVolume)
    var showNotice by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    var isAutoplaying by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (showNotice) {
            Image(
                painter = painterResource(id = belajarBackgroundResId),
                contentDescription = "Belajar Background",
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = belajarNoticeResId),
                    contentDescription = "Belajar Notice",
                    modifier = Modifier
                        .size(500.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { showNotice = false }
                )
            }
        } else {
            AndroidView(
                factory = { context ->
                    val view = LayoutInflater.from(context).inflate(inangBackgroundLayoutResId, null)

                    val soundMap = mapOf(
                        R.id.inang_a to R.raw.inang_a,
                        R.id.inang_ha to R.raw.inang_ha,
                        R.id.inang_ma to R.raw.inang_ma,
                        R.id.inang_na to R.raw.inang_na,
                        R.id.inang_ra to R.raw.inang_ra,
                        R.id.inang_ta to R.raw.inang_ta,
                        R.id.inang_sa to R.raw.inang_sa,
                        R.id.inang_pa to R.raw.inang_pa,
                        R.id.inang_la to R.raw.inang_la,
                        R.id.inang_ga to R.raw.inang_ga,
                        R.id.inang_ja to R.raw.inang_ja,
                        R.id.inang_da to R.raw.inang_da,
                        R.id.inang_nga to R.raw.inang_nga,
                        R.id.inang_ba to R.raw.inang_ba,
                        R.id.inang_wa to R.raw.inang_wa,
                        R.id.inang_ya to R.raw.inang_ya,
                        R.id.inang_nya to R.raw.inang_nya,
                        R.id.inang_i to R.raw.inang_i,
                        R.id.inang_u to R.raw.inang_u
                    )

                    soundMap.forEach { (imageViewId, soundResId) ->
                        view.findViewById<ImageView>(imageViewId)?.setOnClickListener { button ->
                            stopAutoplay(coroutineScope)
                            isAutoplaying = false
                            playSoundWithAnimation(context, soundResId, soundEffectsVolume, button)
                        }
                    }

                    view.findViewById<ImageView>(R.id.autoplay)?.setOnClickListener {
                        if (!isAutoplaying) {
                            isAutoplaying = true
                            coroutineScope.launch {
                                for ((imageViewId, soundResId) in soundMap) {
                                    if (!isAutoplaying) break
                                    val button = view.findViewById<ImageView>(imageViewId)
                                    playSoundSequentiallyWithAnimation(context, soundResId, soundEffectsVolume, button)
                                }
                                isAutoplaying = false
                            }
                        } else {
                            stopAutoplay(coroutineScope)
                            isAutoplaying = false
                        }
                    }

                    view
                },
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Image(
                    painter = painterResource(id = inangBackResId),
                    contentDescription = "Inang Back",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            stopAutoplay(coroutineScope)
                            isAutoplaying = false
                            onBackClick()
                        }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Image(
                    painter = painterResource(id = inangNextResId),
                    contentDescription = "Inang Next",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            stopAutoplay(coroutineScope)
                            isAutoplaying = false
                            onNextClick()
                        }
                )
            }
        }
    }
}

private suspend fun playSoundSequentiallyWithAnimation(context: Context, soundResId: Int, volume: Float, button: View) {
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

private fun playSoundWithAnimation(context: Context, soundResId: Int, volume: Float, button: View) {
    val mediaPlayer = MediaPlayer.create(context, soundResId)
    mediaPlayer.setVolume(volume, volume)
    mediaPlayer.setOnCompletionListener { it.release() }
    mediaPlayer.start()
    animateButton(button, mediaPlayer.duration.toLong())
}

private fun stopAutoplay(coroutineScope: CoroutineScope) {
    coroutineScope.coroutineContext.cancelChildren()
}

private fun animateButton(view: View, duration: Long) {
    val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f, 1f)
    val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f, 1f)
    scaleX.duration = duration
    scaleY.duration = duration
    val animatorSet = AnimatorSet()
    animatorSet.playTogether(scaleX, scaleY)
    animatorSet.interpolator = AccelerateDecelerateInterpolator()
    animatorSet.start()
}
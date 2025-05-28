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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.*
import android.content.SharedPreferences
import com.sahata.R

@Composable
fun AnakScreen(
    anakBackgroundLayoutResId: Int,
    anakNextResId: Int,
    soundEffectsVolume: Float,
    onNextClick: () -> Unit,
    sharedPreferences: SharedPreferences
) {
    val coroutineScope = rememberCoroutineScope()
    var isAutoplaying by remember { mutableStateOf(false) }
    var wasAutoplayingBeforePause by remember { mutableStateOf(false) }
    var showOverlay by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var overlayPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                if (isAutoplaying) {
                    wasAutoplayingBeforePause = true
                    stopAutoplay(coroutineScope)
                    isAutoplaying = false
                }
            }

            override fun onResume(owner: LifecycleOwner) {
                if (wasAutoplayingBeforePause) {
                    wasAutoplayingBeforePause = false
                    isAutoplaying = true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Play one-time overlay audio
    LaunchedEffect(Unit) {
        overlayPlayer = MediaPlayer.create(context, R.raw.anak_penjelasan)
        overlayPlayer?.setVolume(soundEffectsVolume, soundEffectsVolume)
        overlayPlayer?.setOnCompletionListener {
            it.release()
            overlayPlayer = null
        }
        overlayPlayer?.start()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (!showOverlay) {
            AndroidView(
                factory = { context ->
                    val view = LayoutInflater.from(context).inflate(anakBackgroundLayoutResId, null)

                    val soundMap = mapOf(
                        R.id.anak_i to R.raw.anak_i,
                        R.id.anak_u to R.raw.anak_u,
                        R.id.anak_e to R.raw.anak_e,
                        R.id.anak_o to R.raw.anak_o,
                        R.id.anak_ng to R.raw.anak_ng,
                        R.id.anak_pangolat to R.raw.anak_pangolat,
                        R.id.anak_bi to R.raw.anak_bi,
                        R.id.anak_bu to R.raw.anak_bu,
                        R.id.anak_be to R.raw.anak_be,
                        R.id.anak_bo to R.raw.anak_bo,
                        R.id.anak_bang to R.raw.anak_bang,
                        R.id.anak_b to R.raw.anak_b,
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
                            startAutoplay(coroutineScope, soundMap, context, view, soundEffectsVolume)
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
                contentAlignment = Alignment.TopEnd
            ) {
                Image(
                    painter = painterResource(id = anakNextResId),
                    contentDescription = "Anak Next",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            stopAutoplay(coroutineScope)
                            isAutoplaying = false
                            sharedPreferences.edit().putBoolean("hasLearned", true).apply()
                            onNextClick()
                        }
                )
            }
        }

        if (showOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sebelumanaksurat),
                    contentDescription = "Intro Anak",
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Image(
                        painter = painterResource(id = anakNextResId),
                        contentDescription = "Lanjut",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                overlayPlayer?.stop()
                                overlayPlayer?.release()
                                overlayPlayer = null
                                showOverlay = false
                            }
                    )
                }
            }
        }
    }
}

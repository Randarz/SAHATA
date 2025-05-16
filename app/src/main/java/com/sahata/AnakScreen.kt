package com.sahata

import android.view.LayoutInflater
import android.view.View
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.*

@Composable
fun AnakScreen(
    anakBackgroundLayoutResId: Int,
    anakNextResId: Int,
    soundEffectsVolume: Float,
    onNextClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isAutoplaying by remember { mutableStateOf(false) }
    var wasAutoplayingBeforePause by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { context ->
                val view = LayoutInflater.from(context).inflate(anakBackgroundLayoutResId, null)

                val soundMap = mapOf(
                    R.id.anak_i to R.raw.inang_u,
                    R.id.anak_u to R.raw.inang_u,
                    R.id.anak_e to R.raw.inang_u,
                    R.id.anak_o to R.raw.inang_u,
                    R.id.anak_ng to R.raw.inang_u
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

        // Next Button
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
                        onNextClick()
                    }
            )
        }
    }
}
package com.sahata

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*

@Composable
fun AnakScreen(
    anak1LayoutResId: Int,
    anak2LayoutResId: Int,
    anakNextResId: Int,
    anakBackResId: Int,
    soundEffectsVolume: Float,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
    sharedPreferences: SharedPreferences
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 1000
    val lifecycleOwner = LocalLifecycleOwner.current

    var currentPage by remember { mutableStateOf(1) }
    var player: MediaPlayer? by remember { mutableStateOf(null) }
    var isPrepared by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun playButtonSound() {
        val buttonPlayer = MediaPlayer.create(context, R.raw.button)
        buttonPlayer.setVolume(soundEffectsVolume, soundEffectsVolume)
        buttonPlayer.setOnCompletionListener { it.release() }
        buttonPlayer.start()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "scale"
    )

    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) { player?.pause() }
            override fun onResume(owner: LifecycleOwner) {
                MusicController.pauseMusic()
                if (isPrepared) player?.start()
            }
            override fun onDestroy(owner: LifecycleOwner) { player?.release(); player = null }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player?.release(); player = null
        }
    }

    val layoutResId = if (currentPage == 1) anak1LayoutResId else anak2LayoutResId
    val audioResId = if (currentPage == 1) R.raw.anak_penjelasan else R.raw.anak_surat_page2

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        key(currentPage) {
            AndroidView(
                factory = { context ->
                    val view = LayoutInflater.from(context).inflate(layoutResId, null)

                    player?.release()
                    player = MediaPlayer.create(context, audioResId)
                    player?.setVolume(soundEffectsVolume, soundEffectsVolume)
                    player?.setOnPreparedListener {
                        isPrepared = true
                        it.start()

                        coroutineScope.launch {
                            val totalDuration = it.duration.toLong()
                            if (currentPage == 1) animateAnak1Timeline(view, totalDuration)
                            else animateAnak2Timeline(view)
                        }
                    }
                    player?.setOnCompletionListener { it.release(); player = null }
                    player?.start()

                    view
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.TopStart) {
            Image(
                painter = painterResource(id = anakBackResId),
                contentDescription = null,
                modifier = Modifier
                    .width(if (isTablet) 130.dp else 90.dp)
                    .height(if (isTablet) 64.dp else 44.dp)
                    .graphicsLayer { scaleX = scaleAnim; scaleY = scaleAnim }
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                        playButtonSound()
                        player?.stop(); player?.release(); player = null
                        if (currentPage == 1) onBackClick() else currentPage--
                    }
            )
        }

        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.TopEnd) {
            Image(
                painter = painterResource(id = anakNextResId),
                contentDescription = null,
                modifier = Modifier
                    .width(if (isTablet) 130.dp else 90.dp)
                    .height(if (isTablet) 64.dp else 44.dp)
                    .graphicsLayer { scaleX = scaleAnim; scaleY = scaleAnim }
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                        playButtonSound()
                        player?.stop(); player?.release(); player = null
                        if (currentPage == 2) {
                            sharedPreferences.edit().putBoolean("hasLearned", true).apply()
                            onNextClick()
                        } else currentPage++
                    }
            )
        }
    }

    BackHandler {
        player?.stop(); player?.release(); player = null
        if (currentPage == 1) onBackClick() else currentPage--
    }
}

private suspend fun animateAnak1Timeline(view: View, totalDuration: Long) = coroutineScope {
    launch { singleLoop(view.findViewById(R.id.anak_si), 15_000, 22_000) }
    launch { singleLoop(view.findViewById(R.id.anak_su), 23_000, 30_000) }
    launch { singleLoop(view.findViewById(R.id.anak_se), 31_000, 39_000) }
    launch { singleLoop(view.findViewById(R.id.anak_so), 40_000, 46_000) }
    launch { singleLoop(view.findViewById(R.id.anak_sang), 47_000, 52_000) }
    launch { singleLoop(view.findViewById(R.id.anak_s), 53_000, totalDuration) }
}

private suspend fun animateAnak2Timeline(view: View) = coroutineScope {
    launch { singleLoop(view.findViewById(R.id.anak_mini), 8_000, 18_000) }
    launch { singleLoop(view.findViewById(R.id.anak_nugu), 19_000, 29_000) }
    launch { singleLoop(view.findViewById(R.id.anak_pede), 30_000, 42_000) }
    launch { singleLoop(view.findViewById(R.id.anak_loto), 43_000, 54_000) }
    launch { singleLoop(view.findViewById(R.id.anak_minggu), 55_000, 76_000) }
    launch { singleLoop(view.findViewById(R.id.anak_pintu), 77_000, 90_000) }
}

private suspend fun singleLoop(view: View?, startTime: Long, endTime: Long) {
    if (view == null) return
    delay(startTime)
    val loopDuration = endTime - startTime
    val loopStart = System.currentTimeMillis()

    while (System.currentTimeMillis() - loopStart < loopDuration) {
        view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(500).withEndAction {
            view.animate().scaleX(1f).scaleY(1f).setDuration(500)
        }
        delay(1000)
    }
}

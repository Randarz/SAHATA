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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
import androidx.compose.animation.core.*

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
    val context = LocalContext.current

    // 🔊 Sound effect function for button click
    fun playButtonSound() {
        val player = MediaPlayer.create(context, R.raw.button)
        player.setVolume(soundEffectsVolume, soundEffectsVolume)
        player.setOnCompletionListener { it.release() }
        player.start()
    }

    // 🔄 Infinite scale animation for button pulse
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val currentVolume by rememberUpdatedState(soundEffectsVolume)
    var showNotice by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    var isAutoplaying by remember { mutableStateOf(false) }
    var wasAutoplayingBeforePause by remember { mutableStateOf(false) }
    var showMulaianakPopup by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val isTablet = screenWidthDp >= 1000
    val actualBackgroundResId = if (isTablet) R.drawable.belajar_background_tab else belajarBackgroundResId

    LaunchedEffect(Unit) { MusicController.pauseMusic() }

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
                MusicController.pauseMusic() // keep paused after resume
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

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (showNotice) {
            Image(
                painter = painterResource(id = actualBackgroundResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val noticeSize = if (isTablet) 650.dp else 500.dp
                Image(
                    painter = painterResource(id = belajarNoticeResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(noticeSize)
                        .clickable { showNotice = false }
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
                            startAutoplay(coroutineScope, soundMap, context, view, currentVolume)
                        } else {
                            stopAutoplay(coroutineScope)
                            isAutoplaying = false
                        }
                    }

                    view
                },
                modifier = Modifier.fillMaxSize()
            )

            // BACK BUTTON with animation + sound effect
            Box(
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = inangBackResId),
                    contentDescription = null,
                    modifier = Modifier
                        .width(if (isTablet) 130.dp else 90.dp)
                        .height(if (isTablet) 64.dp else 44.dp)
                        .graphicsLayer {
                            scaleX = scaleAnim
                            scaleY = scaleAnim
                        }
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            playButtonSound()
                            stopAutoplay(coroutineScope)
                            isAutoplaying = false
                            onBackClick()
                        }
                )
            }

            // NEXT BUTTON with animation + sound effect
            Box(
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = inangNextResId),
                    contentDescription = null,
                    modifier = Modifier
                        .width(if (isTablet) 130.dp else 90.dp)
                        .height(if (isTablet) 64.dp else 44.dp)
                        .graphicsLayer {
                            scaleX = scaleAnim
                            scaleY = scaleAnim
                        }
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            playButtonSound()
                            stopAutoplay(coroutineScope)
                            isAutoplaying = false
                            showMulaianakPopup = true
                        }
                )
            }
        }

        // Popup for starting AnakScreen
        if (showMulaianakPopup) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f))
                    .clickable {
                        showMulaianakPopup = false
                        MusicController.pauseMusic()
                        onNextClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.new_mulaianak),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

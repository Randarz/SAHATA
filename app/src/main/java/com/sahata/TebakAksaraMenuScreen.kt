package com.sahata

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asAndroidColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun TebakAksaraMenuScreen(
    tebakMenuLayoutResId: Int,
    inangBackResId: Int,
    onBackClick: () -> Unit,
    onLevel1Click: () -> Unit,
    onLevel2Click: () -> Unit,
    onLevel3Click: () -> Unit,
    onLevel4Click: () -> Unit,
    onLevel5Click: () -> Unit,
    sharedPreferences: SharedPreferences
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 1000
    val context = LocalContext.current
    var audioFinished by remember { mutableStateOf(false) }

    var showIntroImage by remember {
        mutableStateOf(!sharedPreferences.getBoolean("hasSeenTebakAksaraInfo", false))
    }
    var currentPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    // Load sharedPreferences for level unlock status
    val level1Completed = sharedPreferences.getBoolean("level1Completed", false)
    val level2Completed = sharedPreferences.getBoolean("level2Completed", false)
    val level3Completed = sharedPreferences.getBoolean("level3Completed", false)
    val level4Completed = sharedPreferences.getBoolean("level4Completed", false)

    fun playSound(resId: Int) {
        currentPlayer?.stop()
        currentPlayer?.release()
        currentPlayer = MediaPlayer.create(context, resId)?.apply {
            setVolume(1.0f, 1.0f)
            setOnCompletionListener {
                it.release()
                if (currentPlayer == it) currentPlayer = null
                audioFinished = true  // <-- mark finished
            }
            start()
        }
    }

    fun playButtonClick() {
        val buttonPlayer = MediaPlayer.create(context, R.raw.button)
        buttonPlayer.setVolume(1.0f, 1.0f)
        buttonPlayer.setOnCompletionListener { it.release() }
        buttonPlayer.start()
    }

    // ✅ Setup infinite pulse animation for buttons
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

    LaunchedEffect(showIntroImage) {
        if (showIntroImage) {
            playSound(R.raw.info_tebak_aksara)
        }
    }

    if (showIntroImage) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = audioFinished) {
                    showIntroImage = false
                    sharedPreferences.edit().putBoolean("hasSeenTebakAksaraInfo", true).apply()
                    currentPlayer?.stop()
                    currentPlayer?.release()
                    currentPlayer = null
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = if (isTablet) R.drawable.tebak_aksara_info_tab else R.drawable.tebak_aksara_info),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val view = LayoutInflater.from(ctx).inflate(tebakMenuLayoutResId, null)

                // LEVEL 1 (always unlocked)
                val level1 = view.findViewById<ImageView>(R.id.level1)
                level1.setOnClickListener {
                    playButtonClick()
                    onLevel1Click()
                }
                applyPulse(level1)

                // LEVEL 2
                val level2 = view.findViewById<ImageView>(R.id.level2)
                if (level1Completed) {
                    level2.setOnClickListener {
                        playButtonClick()
                        onLevel2Click()
                    }
                    applyPulse(level2)
                } else {
                    applyGrayscaleFilter(level2)
                }

                // LEVEL 3
                val level3 = view.findViewById<ImageView>(R.id.level3)
                if (level2Completed) {
                    level3.setOnClickListener {
                        playButtonClick()
                        onLevel3Click()
                    }
                    applyPulse(level3)
                } else {
                    applyGrayscaleFilter(level3)
                }

                // LEVEL 4
                val level4 = view.findViewById<ImageView>(R.id.level4)
                if (level3Completed) {
                    level4.setOnClickListener {
                        playButtonClick()
                        onLevel4Click()
                    }
                    applyPulse(level4)
                } else {
                    applyGrayscaleFilter(level4)
                }

                // LEVEL 5
                val level5 = view.findViewById<ImageView>(R.id.level5)
                if (level4Completed) {
                    level5.setOnClickListener {
                        playButtonClick()
                        onLevel5Click()
                    }
                    applyPulse(level5)
                } else {
                    applyGrayscaleFilter(level5)
                }

                view
            },
            modifier = Modifier.fillMaxSize()
        )

        // BACK BUTTON
        Box(
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            val interactionSource = remember { MutableInteractionSource() }

            val infiniteTransition = rememberInfiniteTransition(label = "pulse_back")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale_back"
            )

            Image(
                painter = painterResource(id = inangBackResId),
                contentDescription = "Back",
                modifier = Modifier
                    .width(if (isTablet) 130.dp else 90.dp)
                    .height(if (isTablet) 64.dp else 44.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource
                    ) {
                        playButtonClick()
                        onBackClick()
                    }
            )
        }
    }

    BackHandler { onBackClick() }
}

private fun applyGrayscaleFilter(imageView: ImageView?) {
    if (imageView == null) return
    val matrix = android.graphics.ColorMatrix().apply { setSaturation(0f) }
    imageView.colorFilter = ColorFilter.colorMatrix(ColorMatrix(matrix.array)).asAndroidColorFilter()
    imageView.isClickable = false
}

// ✅ Apply pulse animation directly on AndroidView ImageView
private fun applyPulse(imageView: ImageView?) {
    if (imageView == null) return

    val fadeIn = AlphaAnimation(0f, 1f).apply {
        duration = 500
        fillAfter = true
    }
    val pulse = ScaleAnimation(
        1f, 1.05f, 1f, 1.05f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    ).apply {
        duration = 1000
        repeatMode = Animation.REVERSE
        repeatCount = Animation.INFINITE
    }

    imageView.startAnimation(fadeIn)
    imageView.postDelayed({
        imageView.startAnimation(pulse)
    }, 500)
}

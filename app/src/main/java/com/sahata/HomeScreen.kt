package com.sahata

import android.media.MediaPlayer
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    backgroundResId: Int,
    settingResId: Int,
    noticeResId: Int,
    onSettingClick: () -> Unit,
    onNoticeClick: () -> Unit,
    onBelajarClick: () -> Unit,
    onBermainClick: () -> Unit
) {
    val context = LocalContext.current
    val settingAlpha = remember { Animatable(0f) }
    val noticeAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { settingAlpha.animateTo(1f, animationSpec = tween(300)) }
        launch { noticeAlpha.animateTo(1f, animationSpec = tween(300)) }
    }

    val floatAngle by rememberInfiniteTransition(label = "float").animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    val settingOffset = IntOffset(
        (cos(floatAngle.toDouble()) * 4f).toInt(),
        (sin(floatAngle.toDouble()) * 6f).toInt()
    )
    val noticeOffset = IntOffset(
        (cos(floatAngle.toDouble()) * 6f).toInt(),
        (sin(floatAngle.toDouble()) * 4f).toInt()
    )

    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.button) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                val view = View.inflate(ctx, R.layout.home_background, null)

                val belajarButton = view.findViewById<ImageView>(R.id.belajar_button)
                val bermainButton = view.findViewById<ImageView>(R.id.bermain_button)

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

                belajarButton.startAnimation(fadeIn)
                bermainButton.startAnimation(fadeIn)

                belajarButton.postDelayed({ belajarButton.startAnimation(pulse) }, 500)
                bermainButton.postDelayed({ bermainButton.startAnimation(pulse) }, 500)

                belajarButton.setOnClickListener {
                    mediaPlayer.start()
                    onBelajarClick()
                }

                bermainButton.setOnClickListener {
                    mediaPlayer.start()
                    onBermainClick()
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
                painter = painterResource(id = settingResId),
                contentDescription = "Setting Button",
                modifier = Modifier
                    .size(50.dp)
                    .offset { settingOffset }
                    .graphicsLayer {
                        alpha = settingAlpha.value
                    }
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        mediaPlayer.start()
                        onSettingClick()
                    }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Image(
                painter = painterResource(id = noticeResId),
                contentDescription = "Notice Button",
                modifier = Modifier
                    .size(50.dp)
                    .offset { noticeOffset }
                    .graphicsLayer {
                        alpha = noticeAlpha.value
                    }
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        mediaPlayer.start()
                        onNoticeClick()
                    }
            )
        }
    }
}

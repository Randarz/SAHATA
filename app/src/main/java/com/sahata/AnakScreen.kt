package com.sahata

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.sahata.R

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
    val coroutineScope = rememberCoroutineScope()

    var currentPage by remember { mutableStateOf(1) }
    var player: MediaPlayer? by remember { mutableStateOf(null) }

    fun playAudio(resId: Int) {
        player?.stop()
        player?.release()
        player = MediaPlayer.create(context, resId)
        player?.setVolume(soundEffectsVolume, soundEffectsVolume)
        player?.setOnCompletionListener {
            it.release()
            player = null
        }
        player?.start()
    }

    LaunchedEffect(currentPage) {
        val audioRes = when (currentPage) {
            1 -> R.raw.anak_penjelasan
            2 -> R.raw.anak_surat_page2
            else -> null
        }
        audioRes?.let { playAudio(it) }
    }

    val layoutResId = when (currentPage) {
        1 -> anak1LayoutResId
        2 -> anak2LayoutResId
        else -> anak1LayoutResId
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        key(currentPage) {
            AndroidView(
                factory = { context ->
                    LayoutInflater.from(context).inflate(layoutResId, null)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // BACK BUTTON
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Image(
                painter = painterResource(id = anakBackResId),
                contentDescription = "Back",
                modifier = Modifier
                    .width(if (isTablet) 130.dp else 90.dp)
                    .height(if (isTablet) 64.dp else 44.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        player?.stop()
                        player?.release()
                        player = null
                        if (currentPage == 1) {
                            onBackClick()
                        } else {
                            currentPage--
                        }
                    }
            )
        }

        // NEXT BUTTON
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Image(
                painter = painterResource(id = anakNextResId),
                contentDescription = "Next",
                modifier = Modifier
                    .width(if (isTablet) 130.dp else 90.dp)
                    .height(if (isTablet) 64.dp else 44.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        player?.stop()
                        player?.release()
                        player = null
                        if (currentPage == 2) {
                            sharedPreferences.edit().putBoolean("hasLearned", true).apply()
                            onNextClick()
                        } else {
                            currentPage++
                        }
                    }
            )
        }
    }

    BackHandler {
        player?.stop()
        player?.release()
        player = null
        if (currentPage == 1) {
            onBackClick()
        } else {
            currentPage--
        }
    }
}

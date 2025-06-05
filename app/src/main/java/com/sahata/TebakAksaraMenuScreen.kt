package com.sahata

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.sahata.R

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

    var showIntroImage by remember {
        mutableStateOf(!sharedPreferences.getBoolean("hasSeenTebakAksaraInfo", false))
    }
    var currentPlayer: MediaPlayer? by remember { mutableStateOf(null) }

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
            }
            start()
        }
    }

    LaunchedEffect(showIntroImage) {
        if (showIntroImage) {
            playSound(R.raw.info_tebak_aksara)
        }
    }

    if (showIntroImage) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
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
            factory = { context ->
                LayoutInflater.from(context).inflate(tebakMenuLayoutResId, null).apply {
                    findViewById<ImageView>(R.id.level1)?.setOnClickListener { onLevel1Click() }

                    val level2 = findViewById<ImageView>(R.id.level2)
                    if (level1Completed) {
                        level2?.setOnClickListener { onLevel2Click() }
                    } else {
                        applyGrayscaleFilter(level2)
                    }

                    val level3 = findViewById<ImageView>(R.id.level3)
                    if (level2Completed) {
                        level3?.setOnClickListener { onLevel3Click() }
                    } else {
                        applyGrayscaleFilter(level3)
                    }

                    val level4 = findViewById<ImageView>(R.id.level4)
                    if (level3Completed) {
                        level4?.setOnClickListener { onLevel4Click() }
                    } else {
                        applyGrayscaleFilter(level4)
                    }

                    val level5 = findViewById<ImageView>(R.id.level5)
                    if (level4Completed) {
                        level5?.setOnClickListener { onLevel5Click() }
                    } else {
                        applyGrayscaleFilter(level5)
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            val interactionSource = remember { MutableInteractionSource() }

            Image(
                painter = painterResource(id = inangBackResId),
                contentDescription = "Back",
                modifier = Modifier
                    .width(if (isTablet) 130.dp else 90.dp)
                    .height(if (isTablet) 64.dp else 44.dp)
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource
                    ) {
                        onBackClick()
                    }
            )
        }
    }

    BackHandler {
        onBackClick()
    }
}

private fun applyGrayscaleFilter(imageView: ImageView?) {
    if (imageView == null) return
    val matrix = android.graphics.ColorMatrix().apply { setSaturation(0f) }
    imageView.colorFilter = ColorFilter.colorMatrix(ColorMatrix(matrix.array)).asAndroidColorFilter()
    imageView.isClickable = false
}

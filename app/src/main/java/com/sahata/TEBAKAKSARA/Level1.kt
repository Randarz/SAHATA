package com.sahata.TEBAKAKSARA

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.sahata.R

@Composable
fun Level1Screen(
    onBackClick: () -> Unit,
    onFinishLevel: () -> Unit,
    sharedPreferences: SharedPreferences,
    soundEffectsVolume: Float
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 1000

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var showPopup by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var showFinalScorePopup by remember { mutableStateOf(false) }
    var showIntroImage by remember { mutableStateOf(true) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var isPrepared by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val correctAnswers = listOf("option2", "option3", "option3", "option4", "option3")
    val questionLayouts = listOf(
        R.layout.lvl1_soal1,
        R.layout.lvl1_soal2,
        R.layout.lvl1_soal3,
        R.layout.lvl1_soal4,
        R.layout.lvl1_soal5
    )
    val MyCustomFont = FontFamily(Font(R.font.my_custom_font))
    val ScoreTotalColor = Color(0xFFFFFFFF)
    var currentPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    fun playSound(resId: Int) {
        currentPlayer?.stop()
        currentPlayer?.release()
        currentPlayer = MediaPlayer.create(context, resId)?.apply {
            val isIntroOrSoalAll = resId == R.raw.lvl1_info || resId == R.raw.lvl1_soal_all
            val volume = if (isIntroOrSoalAll) soundEffectsVolume else soundEffectsVolume * 0.2f
            setVolume(volume, volume)
            setOnCompletionListener {
                it.release()
                if (currentPlayer == it) currentPlayer = null
                isPrepared = false
            }
            isPrepared = true
            start()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = object : androidx.lifecycle.DefaultLifecycleObserver {
            override fun onPause(owner: androidx.lifecycle.LifecycleOwner) {
                currentPlayer?.pause()
            }
            override fun onResume(owner: androidx.lifecycle.LifecycleOwner) {
                if (isPrepared) {
                    currentPlayer?.start()
                }
            }
            override fun onDestroy(owner: androidx.lifecycle.LifecycleOwner) {
                currentPlayer?.release()
                currentPlayer = null
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            currentPlayer?.release()
            currentPlayer = null
        }
    }


    LaunchedEffect(Unit) {
        playSound(R.raw.lvl1_info)
    }

    LaunchedEffect(currentQuestionIndex) {
        if (!showIntroImage) playSound(R.raw.lvl1_soal_all)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = showIntroImage) {
                if (showIntroImage) {
                    showIntroImage = false
                    currentPlayer?.stop()
                    currentPlayer?.release()
                    currentPlayer = null
                    playSound(R.raw.lvl1_soal_all)
                }
            }
    ) {
        if (showIntroImage) {
            Image(
                painter = painterResource(id = if (isTablet) R.drawable.tab_info_lvl1 else R.drawable.hp_info_lvl1),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            return@Box
        }

        val currentLayout = remember(currentQuestionIndex) { questionLayouts[currentQuestionIndex] }

        key(currentQuestionIndex) {
            AndroidView(
                factory = { context ->
                    LayoutInflater.from(context).inflate(currentLayout, null).apply {
                        val options = listOf(
                            findViewById<ImageView>(R.id.option1),
                            findViewById<ImageView>(R.id.option2),
                            findViewById<ImageView>(R.id.option3),
                            findViewById<ImageView>(R.id.option4)
                        )
                        options.forEachIndexed { index, option ->
                            option.setOnClickListener {
                                val selectedOption = "option${index + 1}"
                                isCorrect = selectedOption == correctAnswers[currentQuestionIndex]
                                if (isCorrect) {
                                    score += 20
                                    playSound(R.raw.correct)
                                } else {
                                    playSound(R.raw.incorrect)
                                }
                                showPopup = true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.game_exit_green),
                contentDescription = "Exit Game",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(if (isTablet) 130.dp else 90.dp)
                    .height(if (isTablet) 64.dp else 44.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        currentPlayer?.stop()
                        currentPlayer?.release()
                        currentPlayer = null
                        onBackClick()
                    }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .width(if (isTablet) 130.dp else 90.dp)
                .height(if (isTablet) 64.dp else 44.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.score_background_green),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = "$score",
                fontFamily = MyCustomFont,
                color = Color(0xFFFFFFFF),
                fontSize = if (isTablet) 32.sp else 24.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (showPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        showPopup = false
                        currentPlayer?.stop()
                        currentPlayer?.release()
                        currentPlayer = null
                        if (currentQuestionIndex < questionLayouts.size - 1) {
                            currentQuestionIndex++
                        } else {
                            showFinalScorePopup = true
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = if (isCorrect) {
                        painterResource(id = if (isTablet) R.drawable.soal_benar_tab else R.drawable.soal_benar)
                    } else {
                        painterResource(id = if (isTablet) R.drawable.soal_salah_tab else R.drawable.soal_salah)
                    },
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        if (showFinalScorePopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        currentPlayer?.stop()
                        currentPlayer?.release()
                        currentPlayer = null
                        showFinalScorePopup = false
                        sharedPreferences.edit().putBoolean("level1Completed", true).apply()
                        onFinishLevel()
                    }
            ) {
                Image(
                    painter = painterResource(id = if (isTablet) R.drawable.totalscore_background_tab else R.drawable.totalscore_background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Level 1 telah selesai",
                        fontFamily = MyCustomFont,
                        fontSize = if (isTablet) 40.sp else 30.sp,
                        color = ScoreTotalColor,
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(
                        text = "Nilai Kamu : $score",
                        fontFamily = MyCustomFont,
                        fontSize = if (isTablet) 40.sp else 30.sp,
                        color = ScoreTotalColor,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}

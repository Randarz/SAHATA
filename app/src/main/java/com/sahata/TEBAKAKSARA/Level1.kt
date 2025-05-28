package com.sahata.TEBAKAKSARA

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.sahata.R

@Composable
fun Level1Screen(
    onBackClick: () -> Unit,
    onFinishLevel: () -> Unit,
    sharedPreferences: SharedPreferences,
    soundEffectsVolume: Float
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var showPopup by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var showFinalScorePopup by remember { mutableStateOf(false) }
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

    fun playSound(resId: Int) {
        val player = MediaPlayer.create(context, resId)
        val reducedVolume = soundEffectsVolume * 0.2f
        player.setVolume(reducedVolume, reducedVolume)
        player.setOnCompletionListener { it.release() }
        player.start()
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

        // SCORE: TOP-RIGHT CORNER
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .width(90.dp)
                .height(44.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.score_background),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = "$score",
                fontFamily = MyCustomFont,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (showPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        showPopup = false
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
                        painterResource(id = R.drawable.soal_benar)
                    } else {
                        painterResource(id = R.drawable.soal_salah)
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
                        showFinalScorePopup = false
                        sharedPreferences.edit().putBoolean("level1Completed", true).apply()
                        onFinishLevel()
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.totalscore_background),
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
                        fontSize = 22.sp,
                        color = ScoreTotalColor,
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(
                        text = "Nilai Kamu : $score",
                        fontFamily = MyCustomFont,
                        fontSize = 20.sp,
                        color = ScoreTotalColor,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}

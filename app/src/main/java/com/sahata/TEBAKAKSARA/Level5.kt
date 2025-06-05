package com.sahata.TEBAKAKSARA

import android.content.ClipData
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
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
import com.sahata.R

@Composable
fun Level5Screen(
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

    val context = LocalContext.current
    val MyCustomFont = FontFamily(Font(R.font.my_custom_font))
    val ScoreTotalColor = Color(0xFFFFFFFF)
    var currentPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    fun playSound(resId: Int, fullVolume: Boolean = false) {
        currentPlayer?.stop()
        currentPlayer?.release()
        currentPlayer = MediaPlayer.create(context, resId)?.apply {
            val volume = if (fullVolume) soundEffectsVolume else soundEffectsVolume * 0.2f
            setVolume(volume, volume)
            setOnCompletionListener {
                it.release()
                if (currentPlayer == it) currentPlayer = null
            }
            start()
        }
    }

    val questionLayouts = listOf(
        R.layout.lvl5_soal1,
        R.layout.lvl5_soal2,
        R.layout.lvl5_soal3,
        R.layout.lvl5_soal4,
        R.layout.lvl5_soal5
    )

    val correctAnswers = listOf(
        listOf("option1", "option2"),
        listOf("option1", "option2", "option4"),
        listOf("option3", "option2"),
        listOf("option3", "option2"),
        listOf("option3", "option4")
    )

    val questionSounds = listOf(
        R.raw.lvl5_soal1,
        R.raw.lvl5_soal2,
        R.raw.lvl5_soal3,
        R.raw.lvl5_soal4,
        R.raw.lvl5_soal5
    )

    val optionDrawables = listOf(
        mapOf(
            "option1" to R.drawable.lvl5_soal1_opt1,
            "option2" to R.drawable.lvl5_soal1_opt2,
            "option3" to R.drawable.lvl5_soal1_opt3,
            "option4" to R.drawable.lvl5_soal1_opt4,
        ),
        mapOf(
            "option1" to R.drawable.lvl5_soal2_opt1,
            "option2" to R.drawable.lvl5_soal2_opt2,
            "option3" to R.drawable.lvl5_soal2_opt3,
            "option4" to R.drawable.lvl5_soal2_opt4,
            "option5" to R.drawable.lvl5_soal2_opt5
        ),
        mapOf(
            "option1" to R.drawable.lvl5_soal3_opt1,
            "option2" to R.drawable.lvl5_soal3_opt2,
            "option3" to R.drawable.lvl5_soal3_opt3,
            "option4" to R.drawable.lvl5_soal3_opt4,
        ),
        mapOf(
            "option1" to R.drawable.lvl5_soal4_opt1,
            "option2" to R.drawable.lvl5_soal4_opt2,
            "option3" to R.drawable.lvl5_soal4_opt3,
            "option4" to R.drawable.lvl5_soal4_opt4,
        ),
        mapOf(
            "option1" to R.drawable.lvl5_soal5_opt1,
            "option2" to R.drawable.lvl5_soal5_opt2,
            "option3" to R.drawable.lvl5_soal5_opt3,
            "option4" to R.drawable.lvl5_soal5_opt4,
            )
    )

    LaunchedEffect(Unit) {
        playSound(R.raw.lvl5_info, fullVolume = true)
    }

    LaunchedEffect(currentQuestionIndex) {
        if (!showIntroImage) {
            playSound(questionSounds[currentQuestionIndex], fullVolume = true)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().clickable(enabled = showIntroImage) {
            if (showIntroImage) {
                showIntroImage = false
                currentPlayer?.stop()
                currentPlayer?.release()
                currentPlayer = null
                playSound(questionSounds[currentQuestionIndex], fullVolume = true)
            }
        }
    ) {
        if (showIntroImage) {
            Image(
                painter = painterResource(id = if (isTablet) R.drawable.tab_info_lvl5 else R.drawable.hp_info_lvl5),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            return@Box
        }

        key(currentQuestionIndex) {
            AndroidView(
                factory = { context ->
                    LayoutInflater.from(context).inflate(questionLayouts[currentQuestionIndex], null).apply {
                        val currentDrawables = optionDrawables[currentQuestionIndex]
                        val correctAnswer = correctAnswers[currentQuestionIndex]

                        val optionViews = currentDrawables.keys.mapNotNull { tag ->
                            val id = resources.getIdentifier(tag, "id", context.packageName)
                            findViewById<ImageView?>(id)?.apply {
                                setTag(tag)
                                setOnLongClickListener {
                                    val data = ClipData.newPlainText("tag", tag)
                                    val shadow = View.DragShadowBuilder(this)
                                    startDragAndDrop(data, shadow, null, 0)
                                    visibility = View.INVISIBLE
                                    true
                                }
                            }
                        }.associateBy { it.tag as String }

                        val dropViews = correctAnswer.indices.map { i ->
                            val id = resources.getIdentifier("drop${i + 1}", "id", context.packageName)
                            findViewById<ImageView>(id)
                        }

                        val dropTags = mutableMapOf<ImageView, String?>()
                        dropViews.forEach { dropTags[it] = null }

                        fun checkAnswer() {
                            val userAnswer = dropViews.mapNotNull { dropTags[it] }
                            if (userAnswer.size == correctAnswer.size) {
                                isCorrect = userAnswer == correctAnswer
                                if (isCorrect) {
                                    score += 20
                                    playSound(R.raw.correct)
                                } else {
                                    playSound(R.raw.incorrect)
                                }
                                showPopup = true
                            }
                        }

                        val dragListener = View.OnDragListener { v, event ->
                            when (event.action) {
                                DragEvent.ACTION_DROP -> {
                                    val tag = event.clipData.getItemAt(0)?.text?.toString()
                                    if (tag != null && currentDrawables.containsKey(tag)) {
                                        val drop = v as ImageView
                                        drop.setImageResource(currentDrawables[tag]!!)
                                        dropTags[drop] = tag
                                        checkAnswer()
                                    }
                                    true
                                }
                                DragEvent.ACTION_DRAG_ENDED -> {
                                    optionViews.forEach { (tag, view) ->
                                        if (!dropTags.values.contains(tag)) view.visibility = View.VISIBLE
                                    }
                                    true
                                }
                                else -> true
                            }
                        }

                        dropViews.forEach { it.setOnDragListener(dragListener) }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.game_exit_blue),
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
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                .width(if (isTablet) 130.dp else 90.dp)
                .height(if (isTablet) 64.dp else 44.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.score_background_blue),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = "$score",
                fontFamily = MyCustomFont,
                color = Color.White,
                fontSize = if (isTablet) 32.sp else 24.sp
            )
        }

        if (showPopup) {
            Box(
                modifier = Modifier.fillMaxSize().clickable {
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
                    painter = if (isCorrect)
                        painterResource(id = if (isTablet) R.drawable.soal_benar_tab else R.drawable.soal_benar)
                    else
                        painterResource(id = if (isTablet) R.drawable.soal_salah_tab else R.drawable.soal_salah),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        if (showFinalScorePopup) {
            Box(
                modifier = Modifier.fillMaxSize().clickable {
                    currentPlayer?.stop()
                    currentPlayer?.release()
                    currentPlayer = null
                    showFinalScorePopup = false
                    sharedPreferences.edit().putBoolean("level5Completed", true).apply()
                    onFinishLevel()
                }
            ) {
                Image(
                    painter = painterResource(id = if (isTablet) R.drawable.totalscore_background5_tab else R.drawable.totalscore_background5),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Level 5 telah selesai",
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

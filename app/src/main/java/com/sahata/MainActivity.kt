package com.sahata

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sahata.ui.theme.SAHATATheme
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    var mediaPlayer: MediaPlayer? = null
    var musicVolumeLevel: Int = 3 // Default music volume level (60%)
    var suaraVolumeLevel: Int = 3 // Default suara volume level (60%)
    private var autoplayJob: Job? = null // Job to manage autoplay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize and start background music
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music).apply {
            isLooping = true
            setVolume(musicVolumeLevel * 0.2f, musicVolumeLevel * 0.2f)
            start()
        }

        // Start autoplay logic
        startAutoplay()

        // Postpone immersive mode setup until the decor view is ready
        window.decorView.post {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.apply {
                    hide(android.view.WindowInsets.Type.systemBars())
                    systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        )
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            SAHATATheme {
                var soundEffectsVolume by remember { mutableStateOf(suaraVolumeLevel * 0.2f) }

                AppNavigation(
                    getMusicVolumeLevel = { musicVolumeLevel },
                    setMusicVolumeLevel = { updateMusicVolumeLevel(it) },
                    getSuaraVolumeLevel = { suaraVolumeLevel },
                    setSuaraVolumeLevel = {
                        suaraVolumeLevel = it
                        soundEffectsVolume = suaraVolumeLevel * 0.2f // Update volume dynamically
                    },
                    soundEffectsVolume = soundEffectsVolume // Pass updated volume
                )
            }
        }
    }

    private fun startAutoplay() {
        autoplayJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                // Add your autoplay logic here
                delay(1000) // Example delay for autoplay
            }
        }
    }

    fun updateMusicVolumeLevel(level: Int) {
        musicVolumeLevel = level.coerceIn(0, 5) // Ensure volume level is between 0 and 5
        val volume = musicVolumeLevel * 0.2f
        mediaPlayer?.setVolume(volume, volume)
    }

    override fun onPause() {
        super.onPause()
        // Pause the music when the app is not in the foreground
        mediaPlayer?.pause()

        // Cancel autoplay
        autoplayJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        // Resume the music when the app comes back to the foreground
        mediaPlayer?.start()

        // Restart autoplay
        startAutoplay()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer resources
        mediaPlayer?.release()
        mediaPlayer = null

        // Cancel autoplay
        autoplayJob?.cancel()
    }
}

@Composable
fun AppNavigation(
    getMusicVolumeLevel: () -> Int,
    setMusicVolumeLevel: (Int) -> Unit,
    getSuaraVolumeLevel: () -> Int,
    setSuaraVolumeLevel: (Int) -> Unit,
    soundEffectsVolume: Float // Pass the updated volume
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "loading") {
        composable("loading") {
            LoadingScreen(
                backgroundResId = R.drawable.loading_background,
                titleResId = R.drawable.loading_title,
                frameResId = R.drawable.loading_bar
            ) {
                navController.navigate("home")
            }
        }
        composable("home") {
            HomeScreen(
                backgroundResId = R.drawable.home_background,
                belajarResId = R.drawable.home_belajar,
                bermainResId = R.drawable.home_bermain,
                settingResId = R.drawable.home_setting,
                noticeResId = R.drawable.home_notice,
                onSettingClick = { navController.navigate("setting") },
                onNoticeClick = { navController.navigate("notice") },
                onBelajarClick = { navController.navigate("belajar") }
            )
        }
        composable("setting") {
            SettingScreen(
                getMusicVolumeLevel = getMusicVolumeLevel,
                setMusicVolumeLevel = setMusicVolumeLevel,
                getSuaraVolumeLevel = getSuaraVolumeLevel,
                setSuaraVolumeLevel = setSuaraVolumeLevel,
                onBackToHome = { navController.navigate("home") }
            )
        }
        composable("belajar") {
            BelajarScreen(
                belajarBackgroundResId = R.drawable.belajar_background,
                belajarNoticeResId = R.drawable.belajar_notice,
                inangBackgroundLayoutResId = R.layout.inang_background,
                inangBackResId = R.drawable.inang_back,
                inangNextResId = R.drawable.inang_next,
                onBackClick = { navController.popBackStack() },
                onNextClick = { /* Add navigation logic for the next screen */ },
                soundEffectsVolume = soundEffectsVolume // Pass the updated volume
            )
        }
        composable("notice") {
            NoticeScreen(
                backgroundResId = R.drawable.background_shade,
                fieldResId = R.drawable.notice_field
            )
        }
    }
}

@Composable
fun SettingScreen(
    getMusicVolumeLevel: () -> Int,
    setMusicVolumeLevel: (Int) -> Unit,
    getSuaraVolumeLevel: () -> Int,
    setSuaraVolumeLevel: (Int) -> Unit,
    onBackToHome: () -> Unit
) {
    AndroidView(
        factory = { context: Context ->
            val view = View.inflate(context, R.layout.activity_setting, null)

            val musicBar = view.findViewById<SeekBar>(R.id.setting_barmusik)
            val musicDown = view.findViewById<ImageView>(R.id.setting_musikmin)
            val musicUp = view.findViewById<ImageView>(R.id.setting_musikplus)

            val suaraBar = view.findViewById<SeekBar>(R.id.setting_barsuara)
            val suaraDown = view.findViewById<ImageView>(R.id.setting_suaramin)
            val suaraUp = view.findViewById<ImageView>(R.id.setting_suaraplus)

            musicBar.max = 5
            musicBar.progress = getMusicVolumeLevel()
            musicBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) setMusicVolumeLevel(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
            musicDown.setOnClickListener {
                if (getMusicVolumeLevel() > 0) {
                    setMusicVolumeLevel(getMusicVolumeLevel() - 1)
                    musicBar.progress = getMusicVolumeLevel()
                }
            }
            musicUp.setOnClickListener {
                if (getMusicVolumeLevel() < 5) {
                    setMusicVolumeLevel(getMusicVolumeLevel() + 1)
                    musicBar.progress = getMusicVolumeLevel()
                }
            }

            suaraBar.max = 5
            suaraBar.progress = getSuaraVolumeLevel()
            suaraBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) setSuaraVolumeLevel(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
            suaraDown.setOnClickListener {
                if (getSuaraVolumeLevel() > 0) {
                    setSuaraVolumeLevel(getSuaraVolumeLevel() - 1)
                    suaraBar.progress = getSuaraVolumeLevel()
                }
            }
            suaraUp.setOnClickListener {
                if (getSuaraVolumeLevel() < 5) {
                    setSuaraVolumeLevel(getSuaraVolumeLevel() + 1)
                    suaraBar.progress = getSuaraVolumeLevel()
                }
            }

            val kembaliButton = view.findViewById<ImageView>(R.id.setting_kembali)
            kembaliButton.setOnClickListener {
                onBackToHome()
            }

            view
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun LoadingScreen(
    backgroundResId: Int,
    titleResId: Int,
    frameResId: Int,
    onLoadingComplete: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (progress < 1f) {
            progress += 0.01f
            delay(10)
        }
        onLoadingComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = backgroundResId),
            contentDescription = "Loading Background",
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = titleResId),
                    contentDescription = "Loading Title",
                    modifier = Modifier.size(width = 650.dp, height = 170.dp)
                )

                Box(
                    modifier = Modifier
                        .offset(y = 50.dp)
                        .width(300.dp)
                        .height(50.dp)
                ) {
                    Image(
                        painter = painterResource(id = frameResId),
                        contentDescription = "Loading Bar Frame",
                        modifier = Modifier.fillMaxSize()
                    )
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart)
                            .padding(horizontal = 4.5.dp, vertical = 10.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = colorResource(id = R.color.yellowbar),
                        backgroundColor = Color.Transparent
                    )
                }
            }
        }
    }
}
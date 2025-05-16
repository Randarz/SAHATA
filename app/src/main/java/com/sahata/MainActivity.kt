package com.sahata

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
                        soundEffectsVolume = suaraVolumeLevel * 0.2f
                    },
                    soundEffectsVolume = soundEffectsVolume,
                    onPlaySoundEffect = { playButtonSound(this, soundEffectsVolume) }
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
    soundEffectsVolume: Float,
    onPlaySoundEffect: () -> Unit
)
 {
    val navController = rememberNavController()
    var isLoadingComplete by remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = if (isLoadingComplete) "home" else "loading") {
        composable("loading") {
            LoadingScreen(
                backgroundResId = R.drawable.loading_background,
                titleResId = R.drawable.loading_title,
                frameResId = R.drawable.loading_bar
            ) {
                isLoadingComplete = true
                navController.navigate("home") {
                    popUpTo("loading") { inclusive = true }
                }
            }
        }
        composable("home") {
            HomeScreen(
                backgroundResId = R.drawable.home_background,
                settingResId = R.drawable.home_setting,
                noticeResId = R.drawable.home_notice,
                onSettingClick = { navController.navigate("setting") },
                onNoticeClick = { navController.navigate("notice") },
                onBelajarClick = { navController.navigate("belajar") },
                onBermainClick = {  } // New callback for Bermain button
            )
            BackHandler {
                navController.navigate("exit") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }
        composable("setting") {
            SettingScreen(
                getMusicVolumeLevel = getMusicVolumeLevel,
                setMusicVolumeLevel = setMusicVolumeLevel,
                getSuaraVolumeLevel = getSuaraVolumeLevel,
                setSuaraVolumeLevel = setSuaraVolumeLevel,
                onBackToHome = { navController.navigate("home") },
                onNavigateToExit = { navController.navigate("exit") }
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
                onNextClick = { navController.navigate("anak") }, // Navigate to AnakScreen
                soundEffectsVolume = soundEffectsVolume
            )
        }
        composable("anak") {
            AnakScreen(
                anakBackgroundLayoutResId = R.layout.anak_background,
                anakNextResId = R.drawable.anak_next,
                soundEffectsVolume = soundEffectsVolume,
                onNextClick = { navController.navigate("maribermain") }
            )
        }
        composable("maribermain") {
            MariBermainScreen(
                mariBackgroundLayoutResId = R.layout.mari_background,
                homeButtonResId = R.drawable.bermain_home,
                onHomeClick = { navController.navigate("home") }
            )
        }
        composable("notice") {
            NoticeScreen(
                onBackToHome = { navController.navigate("home") }
            )
        }
        composable("exit") {
            ExitScreen(
                onBackToSetting = { navController.navigate("setting") }
            )
        }
    }
}

@Composable
fun BackHandler(onBackPressed: () -> Unit) {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    DisposableEffect(backPressedDispatcher) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        backPressedDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }
}

@Composable
fun SettingScreen(
    getMusicVolumeLevel: () -> Int,
    setMusicVolumeLevel: (Int) -> Unit,
    getSuaraVolumeLevel: () -> Int,
    setSuaraVolumeLevel: (Int) -> Unit,
    onBackToHome: () -> Unit,
    onNavigateToExit: () -> Unit
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

            val keluarButton = view.findViewById<ImageView>(R.id.setting_keluar)
            keluarButton.setOnClickListener {
                onNavigateToExit()
            }

            view
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun NoticeScreen(
    onBackToHome: () -> Unit
) {
    AndroidView(
        factory = { context ->
            val view = View.inflate(context, R.layout.activity_notice, null)

            // Set up the back button (notice_kembali)
            val backButton = view.findViewById<ImageView>(R.id.notice_kembali)
            backButton.setOnClickListener {
                onBackToHome()
            }

            view
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun ExitScreen(
    onBackToSetting: () -> Unit
) {
    AndroidView(
        factory = { context ->
            val view = View.inflate(context, R.layout.activity_exit, null)

            val kembaliButton = view.findViewById<ImageView>(R.id.exit_kembali)
            kembaliButton.setOnClickListener {
                onBackToSetting()
            }

            val keluarButton = view.findViewById<ImageView>(R.id.exit_keluar)
            keluarButton.setOnClickListener {
                // Close the app
                (context as? Activity)?.finishAffinity()
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

fun playButtonSound(context: Context, volume: Float) {
    val mediaPlayer = MediaPlayer.create(context, R.raw.button)
    mediaPlayer.setVolume(volume, volume)
    mediaPlayer.setOnCompletionListener {
        it.release()
    }
    mediaPlayer.start()
}
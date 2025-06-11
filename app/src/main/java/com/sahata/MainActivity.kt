package com.sahata

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.ui.graphics.graphicsLayer
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sahata.TEBAKAKSARA.*
import com.sahata.ui.theme.SAHATATheme
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {

    var musicVolumeLevel: Int = 1
    var suaraVolumeLevel: Int = 3
    private var autoplayJob: Job? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("TebakAksaraPrefs", Context.MODE_PRIVATE)

        // Setup global MusicController
        MusicController.mediaPlayer = MediaPlayer.create(this, R.raw.app_sound).apply {
            isLooping = true
            setVolume(musicVolumeLevel * 0.1f, musicVolumeLevel * 0.1f)
            start()
        }

        startAutoplay()

        // Apply immersive fullscreen here
        enableImmersiveMode()

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
                    sharedPreferences = sharedPreferences
                )
            }
        }
    }

    private fun startAutoplay() {
        autoplayJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(1000)
            }
        }
    }

    fun updateMusicVolumeLevel(level: Int) {
        musicVolumeLevel = level.coerceIn(0, 5)
        MusicController.updateMusicVolume(level)
    }

    override fun onPause() {
        super.onPause()
        MusicController.pauseMusic()
        autoplayJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        MusicController.startMusic()
        startAutoplay()

        // re-apply immersive fullscreen on resume!
        enableImmersiveMode()
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicController.release()
        autoplayJob?.cancel()
    }

    // Immersive mode function centralized
    private fun enableImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

@Composable
fun AppNavigation(
    getMusicVolumeLevel: () -> Int,
    setMusicVolumeLevel: (Int) -> Unit,
    getSuaraVolumeLevel: () -> Int,
    setSuaraVolumeLevel: (Int) -> Unit,
    soundEffectsVolume: Float,
    sharedPreferences: SharedPreferences
) {
    val navController = rememberNavController()
    var isLoadingComplete by remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = if (isLoadingComplete) "home" else "loading") {

        composable("loading") {
            LoadingScreen(
                backgroundResId = R.drawable.loading_background,
                titleResId = R.drawable.loading_title,
                frameResId = R.drawable.loading_bar,
                navController = navController,
                soundEffectsVolume = soundEffectsVolume
            )
        }

        composable("home") {
            LaunchedEffect(Unit) {
                MusicController.startMusic()
            }
            HomeScreen(
                backgroundResId = R.drawable.home_background,
                settingResId = R.drawable.home_setting,
                noticeResId = R.drawable.home_notice,
                onSettingClick = { navController.navigate("setting") },
                onNoticeClick = { navController.navigate("notice") },
                onBelajarClick = { navController.navigate("belajar") },
                onBermainClick = { navController.navigate("tebak_aksara_menu") },
                sharedPreferences = sharedPreferences,
                soundEffectsVolume = soundEffectsVolume
            )
        }

        composable("setting") {
            SettingScreen(
                getMusicVolumeLevel = getMusicVolumeLevel,
                setMusicVolumeLevel = setMusicVolumeLevel,
                getSuaraVolumeLevel = getSuaraVolumeLevel,
                setSuaraVolumeLevel = setSuaraVolumeLevel,
                onBackToHome = { navController.navigate("home") },
                onNavigateToExit = { navController.navigate("exit") },
                soundEffectsVolume = getSuaraVolumeLevel()
            )
        }
        composable("belajar") {
            // Pause music BEFORE composable is launched
            LaunchedEffect(Unit) { MusicController.pauseMusic() }

            BelajarScreen(
                belajarBackgroundResId = R.drawable.belajar_background,
                belajarNoticeResId = R.drawable.belajar_notice,
                inangBackgroundLayoutResId = R.layout.inang_background,
                inangBackResId = R.drawable.inang_back,
                inangNextResId = R.drawable.inang_next,
                soundEffectsVolume = soundEffectsVolume,
                onBackClick = { navController.popBackStack() },
                onNextClick = { navController.navigate("anak") }
            )
        }
        composable("anak") {
            // Pause music BEFORE composable is launched
            LaunchedEffect(Unit) { MusicController.pauseMusic() }

            AnakScreen(
                anak1LayoutResId = R.layout.anak1_background,
                anak2LayoutResId = R.layout.anak2_background,
                anakNextResId = R.drawable.anak_next,
                anakBackResId = R.drawable.anak_back,
                soundEffectsVolume = soundEffectsVolume,
                sharedPreferences = sharedPreferences,
                onNextClick = { navController.navigate("home") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("mari_bermain") { // Ensure this route exists
            MariBermainScreen(
                mariBackgroundLayoutResId = R.layout.mari_background,
                homeButtonResId = R.drawable.bermain_home,
                onHomeClick = { navController.navigate("home") },
                onPetualanganClick = { navController.navigate("mario_game") },
                onTebakAksaraClick = { navController.navigate("tebak_aksara_menu") } // Navigate to Tebak Aksara Menu
            )
        }
        composable("tebak_aksara_menu") {
            LaunchedEffect(Unit) {
                MusicController.startMusic()
            }
            TebakAksaraMenuScreen(
                tebakMenuLayoutResId = R.layout.tebakmenu,
                onBackClick = { navController.navigate("home") }, // Navigate to MariBermainScreen
                onLevel1Click = { navController.navigate("level1") },
                onLevel2Click = { navController.navigate("level2") },
                onLevel3Click = { navController.navigate("level3") },
                onLevel4Click = { navController.navigate("level4") },
                onLevel5Click = { navController.navigate("level5") },
                sharedPreferences = sharedPreferences,
                inangBackResId = R.drawable.bermain_home
            )
        }
        composable("level1") {
            Level1Screen(
                onBackClick = { navController.popBackStack() },
                onFinishLevel = { navController.navigate("tebak_aksara_menu") },
                sharedPreferences = sharedPreferences,
                soundEffectsVolume = soundEffectsVolume
            )
        }
        composable("level2") {
            Level2Screen(
                onBackClick = { navController.popBackStack() },
                onFinishLevel = { navController.navigate("tebak_aksara_menu") },
                sharedPreferences = sharedPreferences,
                soundEffectsVolume = soundEffectsVolume
            )
        }
        composable("level3") {
            Level3Screen(
                onBackClick = { navController.popBackStack() },
                onFinishLevel = { navController.navigate("tebak_aksara_menu") },
                sharedPreferences = sharedPreferences,
                soundEffectsVolume = soundEffectsVolume
            )
        }
        composable("level4") {
            Level4Screen(
                onBackClick = { navController.popBackStack() },
                onFinishLevel = { navController.navigate("tebak_aksara_menu") },
                sharedPreferences = sharedPreferences,
                soundEffectsVolume = soundEffectsVolume
            )
        }
        composable("level5") {
            Level5Screen(
                onBackClick = { navController.popBackStack() },
                onFinishLevel = { navController.navigate("tebak_aksara_menu") },
                sharedPreferences = sharedPreferences,
                soundEffectsVolume = soundEffectsVolume
            )
        }
        composable("mario_game") {
            MarioGameScreen(onBackClick = { navController.popBackStack() })
        }
        composable("notice") {
            NoticeScreen(
                onBackToHome = { navController.navigate("home") },
                soundEffectsVolume = getSuaraVolumeLevel()
            )
        }
        composable("exit") {
            ExitScreen(
                onBackToSetting = { navController.navigate("setting") },
                soundEffectsVolume = getSuaraVolumeLevel() / 5f
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
fun LoadingScreen(
    backgroundResId: Int,
    titleResId: Int,
    frameResId: Int,
    navController: NavController,
    soundEffectsVolume: Float
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    Log.d("LoadingScreen", "screenWidthDp: $screenWidthDp")

    val isTablet = remember(screenWidthDp) { screenWidthDp >= 1000 }

    // Tablet vs Phone adjustments
    val actualBackgroundResId = if (isTablet) R.drawable.loading_background_tab else backgroundResId
    val titleSize = if (isTablet) Modifier.size(800.dp, 200.dp) else Modifier.size(650.dp, 170.dp)
    val frameWidth = if (isTablet) 400.dp else 300.dp
    val frameHeight = if (isTablet) 70.dp else 50.dp
    val progressBarHeight = if (isTablet) 20.dp else 14.dp
    val verticalOffset = if (isTablet) 100.dp else 50.dp
    val horasHorasResId = if (isTablet) R.drawable.horashoras_tab else R.drawable.horas_horas

    var progress by remember { mutableStateOf(0f) }
    var showPopup by remember { mutableStateOf(false) }
    var horasPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        while (progress < 1f) {
            progress += 0.01f
            delay(20)
        }

        showPopup = true

        val player = MediaPlayer.create(context, R.raw.horas)
        player.setVolume(soundEffectsVolume, soundEffectsVolume)
        player.setOnCompletionListener {
            it.release()
            horasPlayer = null
        }
        player.start()
        horasPlayer = player
    }

    if (showPopup) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    horasPlayer?.stop()
                    horasPlayer?.release()
                    horasPlayer = null
                    navController.navigate("home") {
                        popUpTo("loading") { inclusive = true }
                    }
                }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = horasHorasResId),
                    contentDescription = "Horas Notice",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Image(
                painter = painterResource(id = actualBackgroundResId),
                contentDescription = "Loading Background",
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = titleResId),
                        contentDescription = "Loading Title",
                        modifier = titleSize.graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                    )

                    Box(
                        modifier = Modifier
                            .offset(y = verticalOffset)
                            .width(frameWidth)
                            .height(frameHeight)
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
                                .height(progressBarHeight)
                                .clip(RoundedCornerShape(6.dp)),
                            color = colorResource(id = R.color.yellowbar),
                            backgroundColor = Color.Transparent
                        )
                    }
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



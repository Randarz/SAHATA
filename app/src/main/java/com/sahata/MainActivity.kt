package com.sahata

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sahata.ui.theme.SAHATATheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
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
                backgroundResId = R.drawable.background_shade,
                fieldResId = R.drawable.setting_field
            )
        }
        composable("notice") {
            NoticeScreen(
                backgroundResId = R.drawable.background_shade,
                fieldResId = R.drawable.notice_field
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
                onNextClick = { /* Add navigation logic for the next screen */ }
            )
        }
    }
}

@Composable
fun LoadingScreen(
    backgroundResId: Int,
    titleResId: Int,
    frameResId: Int,
    onLoadingComplete: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }

    // Animate the progress
    LaunchedEffect(Unit) {
        while (progress < 1f) {
            progress += 0.01f
            delay(10) // Adjust speed of animation
        }
        onLoadingComplete() // Trigger navigation
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Fallback color if background image fails
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
                // Title remains fixed
                Image(
                    painter = painterResource(id = titleResId),
                    contentDescription = "Loading Title",
                    modifier = Modifier.size(width = 650.dp, height = 170.dp) // Adjust title size
                )

                // Progress bar directly below the title
                Box(
                    modifier = Modifier
                        .offset(y = 50.dp) // Adjust this value to move the bar down relative to the title
                        .width(300.dp)
                        .height(50.dp) // Adjust to match the frame size
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
                            .padding(horizontal = 13.dp, vertical = 10.dp)
                            .height(14.dp) // Adjust thickness
                            .clip(RoundedCornerShape(6.dp)), // Apply rounded corners
                        color = Color.Green,
                        backgroundColor = Color.Transparent
                    )
                }
            }
        }
    }
}
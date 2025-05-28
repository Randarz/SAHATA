package com.sahata

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asAndroidColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.graphics.ColorMatrix

@Composable
fun TebakAksaraMenuScreen(
    tebakMenuLayoutResId: Int,
    inangBackResId: Int,
    onBackClick: () -> Unit,
    onLevel1Click: () -> Unit,
    onLevel2Click: () -> Unit,
    onLevel3Click: () -> Unit,
    onLevel4Click: () -> Unit,
    sharedPreferences: SharedPreferences
) {
    val level1Completed = sharedPreferences.getBoolean("level1Completed", false)
    val level2Completed = sharedPreferences.getBoolean("level2Completed", false)
    val level3Completed = sharedPreferences.getBoolean("level3Completed", false)

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
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            val interactionSource = remember { MutableInteractionSource() }

            Image(
                painter = painterResource(id = inangBackResId),
                contentDescription = "Back",
                modifier = Modifier
                    .size(50.dp)
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

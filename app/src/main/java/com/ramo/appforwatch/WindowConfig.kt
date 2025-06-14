package com.ramo.appforwatch

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.layout.WindowMetricsCalculator

@Composable
inline fun Dynamic(
    isLandscape: Boolean,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    upper: @Composable () -> Unit,
    lower: @Composable () -> Unit
) {
    if (!isLandscape) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = horizontalAlignment, verticalArrangement = verticalArrangement) {
            upper()
            lower()
        }
    } else {
        Row(Modifier.fillMaxSize(), verticalAlignment = verticalAlignment, horizontalArrangement = horizontalArrangement) {
            Column(Modifier, horizontalAlignment = horizontalAlignment, verticalArrangement = verticalArrangement) {
                upper()
            }
            Column(Modifier, horizontalAlignment = horizontalAlignment, verticalArrangement = verticalArrangement) {
                lower()
            }
        }
    }
}

const val MODE_WEAR            = 0
const val MODE_PHONE           = 1
const val MODE_TABLET          = 2
const val MODE_SPLIT           = 3
const val MODE_DESKTOP         = 4

data class ScreenConfig(
    val mode: Int = MODE_PHONE,
    val ratio: Float = 1F,
    val isLandscape: Boolean = false
)

fun ScreenConfig.sp(i: Int): TextUnit = (i * ratio).sp
fun ScreenConfig.dp(i: Int): Dp = (i * ratio).dp

@Composable
fun rememberScreenConfig(): ScreenConfig {
    val cfg       = LocalConfiguration.current
    val window    = LocalWindowInfo.current
    val density   = LocalDensity.current
    val activity  = LocalActivity.current
    val screenCfg  = remember { mutableStateOf(ScreenConfig()) }

    // Compute once at launch for split‑screen full width
    val fullWidthDpAtLaunch = remember {
        activity?.let {
            val metrics = WindowMetricsCalculator
                .getOrCreate()
                .computeCurrentWindowMetrics(it)
                .bounds
                .width()
            density.run { metrics.toDp().value }
        } ?: density.run { window.containerSize.width.toDp().value }
    }

    LaunchedEffect(window) {
        val widthDp = density.run { window.containerSize.width.toDp().value }
        val heightDp = density.run { window.containerSize.height.toDp().value }
        val isWear = (cfg.uiMode and Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_WATCH
        val isSplit = activity?.isInMultiWindowMode == true

        val landscape = widthDp > heightDp

        val mode = when {
            isWear -> MODE_WEAR
            isSplit -> MODE_SPLIT
            cfg.smallestScreenWidthDp < 600 -> MODE_PHONE
            cfg.smallestScreenWidthDp < 852 -> MODE_TABLET
            else -> MODE_DESKTOP
        }

        val ratio = when (mode) {
            MODE_WEAR -> computeRatioFromRange(
                widthDp = widthDp,
                minWidth = density.run { 320.toDp().value }, // smallest phone width
                maxWidth = density.run { 454.toDp().value }, // largest known tablet width
                minRatio = 1F,
                maxRatio = 1.3F
            ) // fixed for now, can be expanded

            MODE_SPLIT -> {
                val fraction = (widthDp / fullWidthDpAtLaunch).coerceIn(0.25f, 1f)
                1f * fraction // lower scale when split
            }

            MODE_PHONE, MODE_TABLET, MODE_DESKTOP -> {
                computeRatioFromRange(
                    widthDp = widthDp,
                    minWidth = 320F, // smallest phone width
                    maxWidth = 852F, // largest known tablet width
                    minRatio = 1f,
                    maxRatio = 1.5F
                )
            }

            else -> 1f
        }

        screenCfg.value  = ScreenConfig(mode, ratio, landscape)
        android.util.Log.w("======>", density.run { 320.toDp().value }.toString())

        /*android.util.Log.w("======>", landscape.toString())
        android.util.Log.w("======>", widthDp.toString())
        android.util.Log.w("======>", heightDp.toString())
        android.util.Log.w("======>", screenCfg.value.ratio.toString() + "---" + screenCfg.value.mode.toString())*/
    }

    return screenCfg.value
}


private fun computeRatioFromRange(
    widthDp: Float,
    minWidth: Float,
    maxWidth: Float,
    @Suppress("SameParameterValue") minRatio: Float = 1F,
    maxRatio: Float = 1.5f
): Float {
    return when {
        widthDp <= minWidth -> minRatio
        widthDp >= maxWidth -> maxRatio
        else -> {
            val progress = (widthDp - minWidth) / (maxWidth - minWidth)
            minRatio + progress * (maxRatio - minRatio)
        }
    }
}

/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.ramo.watchapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.google.android.gms.wearable.Wearable
import com.ramo.watchapp.WatchClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            Scaffold {
                WatchMusicScreen()
            }
        }
    }
}

@Composable
fun WatchMusicScreen() {
    val context = LocalContext.current
    val client = remember { WatchClient { context } }
    val trackData by client.trackData.collectAsState()

    LaunchedEffect(Unit) {

        Wearable.getDataClient(context).addListener(client)
        Wearable.getMessageClient(context).addListener(client)
    }

    Column(Modifier.fillMaxSize().background(Color(42, 42, 42, 255)), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(20.dp))
        Column(Modifier.size(100.dp).padding()) {
            coil.compose.SubcomposeAsyncImage(
                model = LocalContext.current.imageBuildr(trackData.coverUrl),
                success = { (painter, _) ->
                    Image(
                        contentScale = ContentScale.Crop,
                        painter = painter,
                        contentDescription = "Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(
                                shape = RoundedCornerShape(20.dp)
                            )
                            .background(Color.Transparent)
                    )
                },
                onError = {
                },
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.None,
                contentDescription = "Image"
            )
        }
        Spacer(Modifier.height(7.dp))
        Text(trackData.title, Modifier.padding(), fontSize = 14.sp, color = Color.White)
        Text(trackData.artist, Modifier.padding(), fontSize = 12.sp, color = Color.White)
        Spacer(Modifier.height(7.dp))
        Button({
            client.togglePlayback()
        }) {
            Icon(
                imageVector = if (trackData.isPlaying) Pause else Play,
                contentDescription = null
            )
        }
        Spacer(Modifier)
    }
}


internal val android.content.Context.imageBuildr: (String) -> coil.request.ImageRequest
    get() = {
        coil.request.ImageRequest.Builder(this@imageBuildr)
            .data(it)
            .diskCacheKey(it)
            //.addLastModifiedToFileCacheKey(true)
            .networkCachePolicy(coil.request.CachePolicy.ENABLED)
            .diskCachePolicy(coil.request.CachePolicy.ENABLED)
            .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
            .crossfade(true)
            .build()
    }

val Play: ImageVector
    get() {
        if (_Play != null) return _Play!!

        _Play = ImageVector.Builder(
            name = "Play",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(10.804f, 8f)
                lineTo(5f, 4.633f)
                verticalLineToRelative(6.734f)
                close()
                moveToRelative(0.792f, -0.696f)
                arcToRelative(0.802f, 0.802f, 0f, false, true, 0f, 1.392f)
                lineToRelative(-6.363f, 3.692f)
                curveTo(4.713f, 12.69f, 4f, 12.345f, 4f, 11.692f)
                verticalLineTo(4.308f)
                curveToRelative(0f, -0.653f, 0.713f, -0.998f, 1.233f, -0.696f)
                close()
            }
        }.build()

        return _Play!!
    }

private var _Play: ImageVector? = null


val Pause: ImageVector
    get() {
        if (_Pause != null) return _Pause!!

        _Pause = ImageVector.Builder(
            name = "Pause",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(6f, 3.5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0.5f, 0.5f)
                verticalLineToRelative(8f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, -1f, 0f)
                verticalLineTo(4f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0.5f, -0.5f)
                moveToRelative(4f, 0f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0.5f, 0.5f)
                verticalLineToRelative(8f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, -1f, 0f)
                verticalLineTo(4f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0.5f, -0.5f)
            }
        }.build()

        return _Pause!!
    }

private var _Pause: ImageVector? = null


//@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WatchMusicScreen()
}
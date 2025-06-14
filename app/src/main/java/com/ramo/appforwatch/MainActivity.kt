package com.ramo.appforwatch

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.google.android.gms.wearable.Wearable
import com.ramo.appforwatch.ui.theme.AppForWatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            AppForWatchTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(Modifier.padding(innerPadding)) {
                        MusicScreen()
                    }
                }
            }
        }
    }
}

// Android: Compose UI Entry Point
@Composable
fun MusicScreen() {
    val context = LocalContext.current
    val player = remember { MusicPlayer { context } }
    val trackData by player.trackData.collectAsState()

    LaunchedEffect(Unit) {
        Wearable.getMessageClient(context).addListener(player)
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Music Player", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Spacer(Modifier.height(10.dp))
        coil.compose.SubcomposeAsyncImage(
            model = LocalContext.current.imageBuildr(trackData.coverUrl),
            success = { (painter, _) ->
                Image(
                    contentScale = ContentScale.Crop,
                    painter = painter,
                    contentDescription = "Image",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(
                            shape = RoundedCornerShape(20.dp)
                        )
                        .background(Color.Transparent)
                )
            },
            error = {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "aa")
            },
            onError = {
            },
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.None,
            contentDescription = "Image"
        )
        Spacer(Modifier.height(10.dp))
        Text(trackData.title, Modifier.padding(), fontSize = 22.sp, color = Color.White)
        Text(trackData.artist, Modifier.padding(), fontSize = 18.sp, color = Color.White)
        Spacer(Modifier.height(10.dp))
        Button({
            player.togglePlayback()
        }) {
            Icon(
                imageVector = if (trackData.isPlaying) Pause else Play,
                contentDescription = null
            )
        }
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


//@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppForWatchTheme {
        MusicScreen()
    }
}
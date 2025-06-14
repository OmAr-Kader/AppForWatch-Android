package com.ramo.appforwatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.wearable.Wearable
import com.ramo.appforwatch.ui.theme.AppForWatchTheme
import com.ramo.shared.Pause
import com.ramo.shared.Play
import com.ramo.shared.imageBuildr

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
    val sc = rememberScreenConfig()

    LaunchedEffect(Unit) {
        Wearable.getMessageClient(context).addListener(player)
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Dynamic(isLandscape = sc.isLandscape, horizontalArrangement = Arrangement.SpaceEvenly, upper = {
            Text("Music Player", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Spacer(Modifier.height(sc.dp(10)))
            coil.compose.SubcomposeAsyncImage(
                model = LocalContext.current.imageBuildr(trackData.coverUrl),
                success = { (painter, _) ->
                    Image(
                        contentScale = ContentScale.Crop,
                        painter = painter,
                        contentDescription = "Image",
                        modifier = Modifier
                            .size(sc.dp(200))
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
        }) {
            Spacer(Modifier.height(sc.dp(10)))
            Text(trackData.title, Modifier.padding(), fontSize = sc.sp(22), color = Color.White)
            Text(trackData.artist, Modifier.padding(), fontSize = sc.sp(18), color = Color.White)
            Spacer(Modifier.height(sc.dp(10)))
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
}


//@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppForWatchTheme {
        MusicScreen()
    }
}
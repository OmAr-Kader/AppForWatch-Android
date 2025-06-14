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
import com.ramo.shared.Pause
import com.ramo.shared.Play
import com.ramo.shared.imageBuildr
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.suspendCoroutine

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
    val sc = rememberScreenConfig()

    LaunchedEffect(Unit) {
        coroutineScope {
            Wearable.getDataClient(context).addListener(client)
            Wearable.getMessageClient(context).addListener(client)

        }
        kotlinx.coroutines.delay(100L)
        coroutineScope {
            client.config()
        }
    }

    Column(Modifier.fillMaxSize().background(Color(42, 42, 42, 255)), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(sc.dp(15)))
        Column(Modifier.size(sc.dp(80)).padding()) {
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
                                shape = RoundedCornerShape(15.dp)
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
        Spacer(Modifier.height(sc.dp(6)))
        Text(trackData.title, Modifier.padding(), fontSize = sc.sp(10), color = Color.White)
        Text(trackData.artist, Modifier.padding(), fontSize = sc.sp(8), color = Color.White)
        Spacer(Modifier.height(sc.dp(5)))
        Button(
            modifier = Modifier.size(sc.dp(40)),
            onClick = {
                client.togglePlayback()
            }
        ) {
            Icon(
                imageVector = if (trackData.isPlaying) Pause else Play,
                contentDescription = null
            )
        }
        Spacer(Modifier)
    }
}

//@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WatchMusicScreen()
}
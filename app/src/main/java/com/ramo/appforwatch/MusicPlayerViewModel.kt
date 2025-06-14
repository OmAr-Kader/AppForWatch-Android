package com.ramo.appforwatch

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.ramo.shared.MessageConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject


class MyWearableListenerService : WearableListenerService() {

    @SuppressLint("VisibleForTests")
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        Log.d("WearListenerService", "onDataChanged: ${dataEvents.count} events")

        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem: DataItem = event.dataItem
                if (dataItem.uri.path == "/my_data_path") {
                    val dataMapItem = DataMapItem.fromDataItem(dataItem)
                    val message = dataMapItem.dataMap.getString("message_key")
                    val timestamp = dataMapItem.dataMap.getLong("timestamp")
                    Log.d("WearListenerService", "Received message: $message at $timestamp")
                    // Update UI, save to DB, etc.
                }
            }
        }
    }

    override fun onMessageReceived(p0: MessageEvent) {
        super.onMessageReceived(p0)
        Log.d("WearListenerService", "onMessageReceived: $p0 events")
    }
}

data class MusicTrack(
    val isPlaying: Boolean = false,
    val title: String = "",
    val artist: String = "",
    val coverUrl: String = ""
)

class MusicPlayer(private val fetchContext: () -> Context) : ViewModel(), MessageClient.OnMessageReceivedListener {
    private var mediaPlayer: MediaPlayer? = null

    private val _trackData = MutableStateFlow(MusicTrack(title = "Be Together", artist = "Major Lazer", coverUrl = "https://i1.sndcdn.com/artworks-000118756473-j1o6zg-t500x500.jpg"))
    val trackData = _trackData.asStateFlow()

    fun togglePlayback() {
        if (trackData.value.isPlaying) pause() else play()
    }

    fun play() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(fetchContext(), R.raw.be_together)
        }
        mediaPlayer?.start()
        _trackData.value = trackData.value.copy(isPlaying = true)
        sendPlaybackState()
    }

    fun pause() {
        mediaPlayer?.pause()
        _trackData.value = trackData.value.copy(isPlaying = false)
        sendPlaybackState()
    }

    @SuppressLint("VisibleForTests")
    private fun sendPlaybackState(id: String = "from_user") {
        viewModelScope.launch(Dispatchers.Default) {
            Wearable.getNodeClient(fetchContext()).connectedNodes.addOnCompleteListener {
                it.result.also { nodes ->
                    if (nodes.isNotEmpty()) {
                        val dataMapRequest = PutDataMapRequest.create("/music_state")
                        val dataMap = dataMapRequest.dataMap
                        trackData.value.apply {
                            dataMap.putString(MessageConstants.KEY_ACTION, MessageConstants.ACTION_STATE)
                            dataMap.putBoolean(MessageConstants.KEY_IS_PLAYING, isPlaying)
                            dataMap.putString(MessageConstants.KEY_TITLE, title)
                            dataMap.putString(MessageConstants.KEY_ARTIST, artist)
                            dataMap.putString(MessageConstants.KEY_COVER_URL, coverUrl)
                            dataMap.putString(MessageConstants.KEY_ID, id)
                        }
                        val request = dataMapRequest.asPutDataRequest()
                        viewModelScope.launch(Dispatchers.Default) {
                            Wearable.getDataClient(fetchContext()).putDataItem(request)
                        }
                    } else {
                        Log.w("MusicPlayer", "No connected wearable devices found.")
                    }
                }

            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        // Handle incoming Messages
        Log.i("MyActivity", "Activity received: $messageEvent")
        Log.w("MusicPlayer", "${messageEvent.data}")
        val message = messageEvent.data.decodeToString()
        val json = JSONObject(message)
        Log.w("onMessageReceived", "${json.getString(MessageConstants.KEY_ACTION)}")
        when (json.getString(MessageConstants.KEY_ACTION)) {
            MessageConstants.ACTION_TOGGLE -> togglePlayback()
            MessageConstants.ACTION_CONFIG -> sendPlaybackState(id = messageEvent.requestId.toString())
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

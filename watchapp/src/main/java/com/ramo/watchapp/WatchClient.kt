package com.ramo.watchapp

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.ramo.shared.MessageConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

class MyWearableListenerService : WearableListenerService() {

    @SuppressLint("VisibleForTests")
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        Log.d("WearListenerService ==> WearOS", "onDataChanged: ${dataEvents.count} events")

        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem: DataItem = event.dataItem
                if (dataItem.uri.path == "/my_data_path") {
                    val dataMapItem = DataMapItem.fromDataItem(dataItem)
                    val message = dataMapItem.dataMap.getString("message_key")
                    val timestamp = dataMapItem.dataMap.getLong("timestamp")
                    Log.d("WearListenerService ==> WearOS", "Received message: $message at $timestamp")
                    // Update UI, save to DB, etc.
                }
            }
        }
    }

    override fun onMessageReceived(p0: MessageEvent) {
        super.onMessageReceived(p0)
        Log.d("WearListenerService ==> WearOS", "onMessageReceived: $p0 events")
    }
}

data class MusicTrack(
    val isPlaying: Boolean = false,
    val title: String = "",
    val artist: String = "",
    val coverUrl: String = ""
)

class WatchClient(private val fetchContext: () -> Context) : ViewModel(), DataClient.OnDataChangedListener, MessageClient.OnMessageReceivedListener {

    private val _trackData = MutableStateFlow(MusicTrack())
    val trackData = _trackData.asStateFlow()

    @SuppressLint("VisibleForTests")
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.w("MusicPlayer ==>!!!", "$dataEvents")
        for (event in dataEvents) {
            val item = event.dataItem
            if (item.uri.path == "/music_state") {
                Log.w("MusicPlayer ==>!!!", "${DataMapItem.fromDataItem(item).dataMap}")
                val map = DataMapItem.fromDataItem(item).dataMap
                if (map.getString(MessageConstants.KEY_ACTION) == MessageConstants.ACTION_STATE) {
                    val isPlaying = map.getBoolean(MessageConstants.KEY_IS_PLAYING)
                    val title = map.getString(MessageConstants.KEY_TITLE).toString()
                    val artist = map.getString(MessageConstants.KEY_ARTIST).toString()
                    val coverUrl = map.getString(MessageConstants.KEY_COVER_URL).toString()
                    _trackData.value = MusicTrack(isPlaying = isPlaying, title = title, artist = artist, coverUrl = coverUrl)
                }
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        // Handle incoming Messages
        Log.i("MyActivity", "Activity received: $messageEvent")
        if (messageEvent.path == "/my_message_path") {
            val message = String(messageEvent.data, Charsets.UTF_8)
            Log.i("MyActivity", "Activity received Message: $message")
            // Update your Compose State here directly or via a ViewModel
        }
    }

    fun config() {
        sendMessage("/config", MessageConstants.ACTION_CONFIG)
    }

    fun togglePlayback() {
        sendMessage("/toggle", MessageConstants.ACTION_TOGGLE)
    }

    private fun sendMessage(path: String, action: String) {
        val message = JSONObject().apply {
            put(MessageConstants.KEY_ACTION, action)
        }.toString().toByteArray()

        viewModelScope.launch(Dispatchers.Default) {
            Wearable.getNodeClient(fetchContext()).connectedNodes.addOnCompleteListener {
                it.result.also { nodes ->
                    for (node in nodes) {
                        if (node.isNearby) {
                            viewModelScope.launch(Dispatchers.Default) {
                                val let = Wearable.getMessageClient(fetchContext()).sendMessage(node.id, path, message).await()
                                Log.w("MusicPlayer ==>!!!", "$let")
                            }
                        }
                    }
                }
            }
        }
    }

}


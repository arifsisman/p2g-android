package vip.yazilim.p2g.android.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_ROOM_SOCKET_CLOSED
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_SONG_LIST_RECEIVED
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_STRING_ACTIVITY
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_STRING_SERVICE
import vip.yazilim.p2g.android.model.p2g.Song
import vip.yazilim.p2g.android.util.gson.ThreeTenGsonAdapter
import vip.yazilim.p2g.android.util.helper.TAG
import vip.yazilim.p2g.android.util.stomp.WebSocketClient


/**
 * @author mustafaarifsisman - 24.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomWebSocketService : Service() {
    private var roomId: Long? = null
    private lateinit var roomWSClient: StompClient

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val serviceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.v(TAG, "Sending broadcast to activity")
            sendBroadcast()
        }
    }

    private fun sendBroadcast() {
        val intent = Intent()
        intent.action = ACTION_STRING_ACTIVITY
        sendBroadcast(intent)
    }

    private fun sendBroadcastSongList(songList: MutableList<Song>) {
        Log.v(TAG, "Sending broadcastSongList to activity")
        val intent = Intent()
        intent.action = ACTION_SONG_LIST_RECEIVED
        intent.putParcelableArrayListExtra("songList", ArrayList<Parcelable>(songList))
        sendBroadcast(intent)
    }

    private fun sendBroadcastSocketClosed() {
        val intent = Intent()
        intent.action = ACTION_ROOM_SOCKET_CLOSED
        sendBroadcast(intent)
    }

    override fun onCreate() {
        super.onCreate()
        Log.v(TAG, "onCreate")

        val intentFilter = IntentFilter(ACTION_STRING_SERVICE)
        registerReceiver(serviceReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
        unregisterReceiver(serviceReceiver)

        if (this::roomWSClient.isInitialized) {
            roomWSClient.disconnect()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        roomId = intent?.getLongExtra("roomId", -1L)
        roomId?.run {
            connectWebSocket(this)
            subscribe("/p2g/room/${this}/songs")
        }

        return START_STICKY
    }

    private fun connectWebSocket(roomId: Long) {
        roomWSClient = WebSocketClient.getRoomWebSocketClient(roomId)
        roomWSClient.run {
            connect()

            lifecycle()
                .subscribe({
                    when (it.type) {
                        LifecycleEvent.Type.OPENED -> {
                            Log.i(TAG, it.toString())
                        }
                        LifecycleEvent.Type.CLOSED -> {
                            sendBroadcastSocketClosed()
                            Log.i(TAG, it.toString())
                        }
                        LifecycleEvent.Type.ERROR -> {
                            sendBroadcastSocketClosed()
                            Log.i(TAG, it.toString())
                        }
                        else -> Log.i(TAG, it.toString())
                    }
                }, { t: Throwable? ->
                    Log.v(TAG, t?.message.toString())
                })
        }
    }

    private fun subscribe(destinationPath: String) {
        roomWSClient.run {
            topic(destinationPath)
                .subscribe({
                    val json = it.payload
                    Log.v(TAG, json)

                    val gsonBuilder = GsonBuilder()
                    val gson = ThreeTenGsonAdapter.registerLocalDateTime(gsonBuilder).create()

                    val songList = gson.fromJson<MutableList<Song>>(json)

                    sendBroadcastSongList(songList)
                }, { t: Throwable? -> Log.v(TAG, t?.message.toString()) })
        }
    }

    private inline fun <reified T> Gson.fromJson(json: String): T =
        fromJson<T>(json, object : TypeToken<T>() {}.type)
}
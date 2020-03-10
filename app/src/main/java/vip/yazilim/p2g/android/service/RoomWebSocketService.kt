package vip.yazilim.p2g.android.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_ROOM_SOCKET_ERROR
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_ROOM_STATUS
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_SONG_LIST_RECEIVED
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_STRING_ACTIVITY
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_STRING_SERVICE
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_USER_LIST_RECEIVED
import vip.yazilim.p2g.android.constant.enums.RoomStatus
import vip.yazilim.p2g.android.entity.Song
import vip.yazilim.p2g.android.model.p2g.RoomUserModel
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

    private fun sendBroadcastUserList(userList: MutableList<RoomUserModel>) {
        Log.v(TAG, "Sending broadcastUserList to activity")
        val intent = Intent()
        intent.action = ACTION_USER_LIST_RECEIVED
        intent.putParcelableArrayListExtra("userList", ArrayList<Parcelable>(userList))
        sendBroadcast(intent)
    }

    private fun sendBroadcastRoomStatus(status: String) {
        Log.v(TAG, "Sending broadcastRoomStatus to activity")
        val intent = Intent()
        intent.action = ACTION_ROOM_STATUS
        intent.putExtra("roomStatus", status)
        sendBroadcast(intent)
    }

    private fun sendBroadcastSocketClosed() {
        val intent = Intent()
        intent.action = ACTION_ROOM_SOCKET_ERROR
        sendBroadcast(intent)
    }

    override fun onCreate() {
        super.onCreate()
        Log.v(TAG, "onCreate")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground()
        } else {
            startForeground(2, Notification())
        }

        val intentFilter = IntentFilter(ACTION_STRING_SERVICE)
        registerReceiver(serviceReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
        unregisterReceiver(serviceReceiver)

        if (this::roomWSClient.isInitialized && roomWSClient.isConnected) {
            roomWSClient.disconnect()
            if (!roomWSClient.isConnected) Log.v(TAG, "Disconnected from RoomWebSocket[$roomId]")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        roomId = intent?.getLongExtra("roomId", -1L)
        roomId?.run {
            connectWebSocket(this)
            subscribeRoomSongs("/p2g/room/${this}/songs")
            subscribeRoomUsers("/p2g/room/${this}/users")
            subscribeRoomStatus("/p2g/room/${this}/status")
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
                            Log.i(TAG, it.toString())
                            sendBroadcastSocketClosed()
                        }
                        LifecycleEvent.Type.ERROR -> {
                            Log.i(TAG, it.toString())
                            sendBroadcastSocketClosed()
                        }
                        else -> Log.i(TAG, it.toString())
                    }
                }, { t: Throwable? ->
                    Log.v(TAG, t?.message.toString())
                })
        }
    }

//    private fun socketOnErrorHandler(roomId: Long) {
//        roomWSClient = WebSocketClient.getRoomWebSocketClient(roomId)
//        roomWSClient.reconnect()
//
//        if (roomWSClient.isConnected) {
//            subscribeRoomSongs("/p2g/room/${this}/songs")
//            subscribeRoomUsers("/p2g/room/${this}/users")
//            subscribeRoomStatus("/p2g/room/${this}/status")
//        }
//    }

    private fun subscribeRoomSongs(songsPath: String) {
        roomWSClient.run {
            topic(songsPath)
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

    private fun subscribeRoomUsers(usersPath: String) {
        roomWSClient.run {
            topic(usersPath)
                .subscribe({
                    val json = it.payload
                    Log.v(TAG, json)

                    val gsonBuilder = GsonBuilder()
                    val gson = ThreeTenGsonAdapter.registerLocalDateTime(gsonBuilder).create()

                    val userList = gson.fromJson<MutableList<RoomUserModel>>(json)

                    sendBroadcastUserList(userList)
                }, { t: Throwable? -> Log.v(TAG, t?.message.toString()) })
        }
    }

    private fun subscribeRoomStatus(statusPath: String) {
        roomWSClient.run {
            topic(statusPath)
                .subscribe({
                    val json = it.payload
                    Log.v(TAG, json)

                    val gson = GsonBuilder().create()
                    val roomStatus = gson.fromJson<RoomStatus>(json)

                    sendBroadcastRoomStatus(roomStatus.status)
                }, { t: Throwable? -> Log.v(TAG, t?.message.toString()) })
        }
    }

    private inline fun <reified T> Gson.fromJson(json: String): T =
        fromJson<T>(json, object : TypeToken<T>() {}.type)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "vip.yazilim.p2g"
        val channelName = "Play2Gether Room Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("App is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }
}
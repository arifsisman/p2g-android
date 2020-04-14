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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.constant.GeneralConstants.WEBSOCKET_RECONNECT_DELAY
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_MESSAGE_RECEIVE
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_MESSAGE_SEND
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_ROOM_SOCKET_CLOSED
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_ROOM_SOCKET_CONNECTED
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_ROOM_SOCKET_RECONNECTING
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_ROOM_STATUS_RECEIVE
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_SONG_LIST_RECEIVE
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_USER_LIST_RECEIVE
import vip.yazilim.p2g.android.constant.WebSocketActions.CHECK_WEBSOCKET_CONNECTION
import vip.yazilim.p2g.android.entity.Song
import vip.yazilim.p2g.android.model.p2g.ChatMessage
import vip.yazilim.p2g.android.model.p2g.RoomStatusModel
import vip.yazilim.p2g.android.model.p2g.RoomUserModel
import vip.yazilim.p2g.android.util.event.UnauthorizedEvent
import vip.yazilim.p2g.android.util.gson.ThreeTenGsonAdapter
import vip.yazilim.p2g.android.util.helper.TAG
import kotlin.coroutines.CoroutineContext


/**
 * @author mustafaarifsisman - 24.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomWebSocketService : Service(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    private var roomId: Long? = null
    private lateinit var roomWSClient: StompClient
    private val gsonBuilder = GsonBuilder()
    private val gson: Gson = ThreeTenGsonAdapter.registerLocalDateTime(gsonBuilder).create()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val serviceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_MESSAGE_SEND -> {
                    Log.v(TAG, "Sending chatMessage")
                    val chatMessage = intent.getParcelableExtra<ChatMessage>(ACTION_MESSAGE_SEND)
                    roomWSClient.send("/p2g/room/${roomId}/send", gson.toJson(chatMessage))
                        .subscribe({}, { t: Throwable? ->
                            Log.v(TAG, t?.message.toString())
                        })
                }
                CHECK_WEBSOCKET_CONNECTION -> {
                    Log.v(TAG, "Checking the websocket connection")

                    launch {
                        delay(WEBSOCKET_RECONNECT_DELAY)

                        if (!roomWSClient.isConnected) {
                            Log.v(TAG, "Trying to reconnect the websocket")
                            sendBroadcast(Intent(ACTION_ROOM_SOCKET_RECONNECTING))
                            roomId?.let { connectWebSocket(it) }
                        } else {
                            Log.v(TAG, "Connected the websocket")
                        }
                    }

                }
            }
        }
    }

    private fun sendBroadcastSongList(songList: MutableList<Song>) {
        Log.v(TAG, "Sending broadcastSongList to activity")
        val intent = Intent()
        intent.action = ACTION_SONG_LIST_RECEIVE
        intent.putParcelableArrayListExtra(
            ACTION_SONG_LIST_RECEIVE,
            ArrayList<Parcelable>(songList)
        )
        sendBroadcast(intent)
    }

    private fun sendBroadcastUserList(userList: MutableList<RoomUserModel>) {
        Log.v(TAG, "Sending broadcastUserList to activity")
        val intent = Intent()
        intent.action = ACTION_USER_LIST_RECEIVE
        intent.putParcelableArrayListExtra(
            ACTION_USER_LIST_RECEIVE,
            ArrayList<Parcelable>(userList)
        )
        sendBroadcast(intent)
    }

    private fun sendBroadcastChatMessage(chatMessage: ChatMessage) {
        Log.v(TAG, "Sending broadcastChatMessage to activity")
        val intent = Intent()
        intent.action = ACTION_MESSAGE_RECEIVE
        intent.putExtra(ACTION_MESSAGE_RECEIVE, chatMessage)
        sendBroadcast(intent)
    }

    private fun sendBroadcastRoomStatus(roomStatusModel: RoomStatusModel) {
        Log.v(TAG, "Sending broadcastRoomStatus to activity")
        val intent = Intent()
        intent.action = ACTION_ROOM_STATUS_RECEIVE
        intent.putExtra(ACTION_ROOM_STATUS_RECEIVE, roomStatusModel)
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

        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_MESSAGE_SEND)
        intentFilter.addAction(CHECK_WEBSOCKET_CONNECTION)
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
        if (roomId == null) {
            roomId = intent?.getLongExtra("roomId", -1L)
            roomId?.let { connectWebSocket(it) }
        }

        return START_STICKY
    }

    private fun connectWebSocket(roomId: Long) {
        roomWSClient = Api.roomWebSocketClient(roomId)
        roomWSClient.run {
            connect()

            lifecycle()
                .subscribe({ lifecycleEvent ->
                    when (lifecycleEvent.type) {
                        LifecycleEvent.Type.OPENED -> {
                            topic("/p2g/room/$roomId/songs")
                                .subscribe { msg ->
                                    val json = msg.payload
                                    val songList = gson.fromJson<MutableList<Song>>(json)
                                    sendBroadcastSongList(songList)
                                }

                            topic("/p2g/room/$roomId/users")
                                .subscribe { msg ->
                                    val userList =
                                        gson.fromJson<MutableList<RoomUserModel>>(msg.payload)
                                    sendBroadcastUserList(userList)
                                }

                            topic("/p2g/room/$roomId/messages")
                                .subscribe { msg ->
                                    val chatMessage = gson.fromJson<ChatMessage>(msg.payload)
                                    sendBroadcastChatMessage(chatMessage)
                                }

                            topic("/p2g/room/$roomId/status")
                                .subscribe { msg ->
                                    val roomStatusModel =
                                        gson.fromJson<RoomStatusModel>(msg.payload)
                                    sendBroadcastRoomStatus(roomStatusModel)
                                }

                            sendBroadcast(Intent(ACTION_ROOM_SOCKET_CONNECTED))
                        }
                        LifecycleEvent.Type.CLOSED -> {
                            sendBroadcast(Intent(ACTION_ROOM_SOCKET_CLOSED))
                        }
                        else -> {
                            EventBus.getDefault().post(UnauthorizedEvent.instance)
                        }
                    }
                }, {
                    EventBus.getDefault().post(UnauthorizedEvent.instance)
                })
        }
    }

    private inline fun <reified T> Gson.fromJson(json: String): T =
        fromJson(json, object : TypeToken<T>() {}.type)

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
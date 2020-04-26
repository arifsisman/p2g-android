package vip.yazilim.p2g.android.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.MainActivity
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_ROOM_INVITE_RECEIVE
import vip.yazilim.p2g.android.entity.RoomInvite
import vip.yazilim.p2g.android.model.p2g.RoomInviteModel
import vip.yazilim.p2g.android.util.gson.ThreeTenGsonAdapter
import vip.yazilim.p2g.android.util.helper.TAG


/**
 * @author mustafaarifsisman - 13.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class UserWebSocketService : Service() {

    private var userId: String? = null
    private lateinit var userWSClient: StompClient
    private val gsonBuilder = GsonBuilder()
    private val gson: Gson = ThreeTenGsonAdapter.registerLocalDateTime(gsonBuilder).create()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val serviceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
        }
    }

    private fun sendBroadcastRoomInvite(roomInviteModel: RoomInviteModel) {
        Log.v(TAG, "Sending broadcastRoomInvite to fragment")
        val intent = Intent()
        intent.action = ACTION_ROOM_INVITE_RECEIVE
        intent.putExtra("roomInviteModel", roomInviteModel)
        sendBroadcast(intent)
    }

    override fun onCreate() {
        super.onCreate()
        Log.v(TAG, "onCreate")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground()
        } else {
            startForeground(1, Notification())
        }

        val intentFilter = IntentFilter()
        registerReceiver(serviceReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
        unregisterReceiver(serviceReceiver)

        if (this::userWSClient.isInitialized) {
            userWSClient.disconnect()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        userId = intent?.getStringExtra("userId")
        userId?.run { connectWebSocket(this) }

        return START_STICKY
    }

    private fun connectWebSocket(userId: String) {
        try {
            val userWsClientSafe = Api.userWebSocketClient(userId)
            if (userWsClientSafe != null) {
                userWSClient = userWsClientSafe
            }
        } catch (ignored: Exception) {
            return
        }

        if (this::userWSClient.isInitialized) {
            userWSClient.run {
                connect()

                lifecycle()
                    .subscribe { lifecycleEvent ->
                        when (lifecycleEvent.type) {
                            LifecycleEvent.Type.OPENED -> {
                                topic("/p2g/user/$userId/invites")
                                    .subscribe { msg ->
                                        val roomInviteModel =
                                            gson.fromJson(msg.payload, RoomInviteModel::class.java)

                                        sendBroadcastRoomInvite(roomInviteModel)
                                        showInviteNotification(roomInviteModel.roomInvite)
                                    }
                            }
                            LifecycleEvent.Type.CLOSED -> {
                            }
                            LifecycleEvent.Type.ERROR -> {
                            }
                            else -> {
                            }
                        }
                    }
            }
        }
    }

    private fun showInviteNotification(roomInvite: RoomInvite) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "p2g_room_invite"
            val channelName: CharSequence = "Play2Gether"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern =
                longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification: Notification =
            NotificationCompat.Builder(baseContext, "room_invite")
                .setChannelId("p2g_room_invite")
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.mipmap.ic_launcher_foreground
                    )
                )
                .setContentTitle("Play2Gether")
                .setContentText(roomInvite.inviterId + " invited you to Play2Gether!")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .build()

        notificationManager.notify(0, notification)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "vip.yazilim.p2g"
        val channelName = "Play2Gether Invite Service"
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
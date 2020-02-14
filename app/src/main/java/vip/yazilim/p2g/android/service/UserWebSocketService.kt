package vip.yazilim.p2g.android.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import vip.yazilim.p2g.android.constant.GeneralConstants
import vip.yazilim.p2g.android.util.stomp.WebSocketClient

/**
 * @author mustafaarifsisman - 13.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class UserWebSocketService : Service() {
    private var userId: String? = null
    private lateinit var userWSClient: StompClient

    companion object {
        private val TAG = UserWebSocketService::class.simpleName
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        userId = intent?.getStringExtra("userId")
        userId?.run {
            connectWebSocket(this)
            subscribe("/p2g/user/${this}/invites")
        }

        return START_STICKY
    }

    private fun connectWebSocket(userId: String) {
        userWSClient = WebSocketClient.getUserWebSocketClient(userId)
        userWSClient.run {
            connect()

            lifecycle()
                .subscribe({
                    when (it.type) {
                        LifecycleEvent.Type.OPENED -> {
                            Log.i(TAG, it.toString())
                        }
                        LifecycleEvent.Type.CLOSED -> {
                            Log.i(TAG, it.toString())
                        }
                        LifecycleEvent.Type.ERROR -> {
                            Log.i(TAG, it.toString())
                        }
                        else -> Log.i(TAG, it.toString())
                    }
                }, { t: Throwable? ->
                    Log.d(TAG, t?.message.toString())
                })
        }
    }

    private fun subscribe(destinationPath: String) {
        userWSClient.run {
            topic(destinationPath)
                .subscribe({
                    Log.d(GeneralConstants.LOG_TAG, it.payload)
                }, { t: Throwable? -> Log.d(GeneralConstants.LOG_TAG, t?.message.toString()) })
        }
    }

    private fun disconnect() {
        if (this::userWSClient.isInitialized) {
            userWSClient.disconnect()
        }
    }

}
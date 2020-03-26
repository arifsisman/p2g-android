package vip.yazilim.p2g.android.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.util.refrofit.Singleton

/**
 * @author mustafaarifsisman - 25.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class LogoutService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        request(Singleton.apiClient().logout(), null)
    }
}
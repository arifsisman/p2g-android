package vip.yazilim.p2g.android

import android.app.Activity
import android.app.Application

/**
 * @author mustafaarifsisman - 08.04.2020
 * @contact mustafaarifsisman@gmail.com
 */
class Play2GetherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: Play2GetherApplication private set
        lateinit var currentActivity: Activity
        lateinit var userName: String
        lateinit var userId: String
        lateinit var accessToken: String
    }
}
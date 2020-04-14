package vip.yazilim.p2g.android

import android.app.Activity
import android.app.Application
import org.greenrobot.eventbus.EventBus
import vip.yazilim.p2g.android.entity.User
import vip.yazilim.p2g.android.util.event.UnauthorizedEvent

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
        var instance: Play2GetherApplication? = null
            get() {
                return if (field != null) {
                    field
                } else {
                    EventBus.getDefault().post(UnauthorizedEvent.instance)
                    field
                }
            }
        var currentActivity: Activity? = null
            get() {
                return if (field != null) {
                    field
                } else {
                    EventBus.getDefault().post(UnauthorizedEvent.instance)
                    field
                }
            }
        var user: User? = null
            get() {
                return if (field != null) {
                    field
                } else {
                    EventBus.getDefault().post(UnauthorizedEvent.instance)
                    field
                }
            }
        var accessToken: String? = null
            get() {
                return if (field != null) {
                    field
                } else {
                    EventBus.getDefault().post(UnauthorizedEvent.instance)
                    field
                }
            }
    }
}
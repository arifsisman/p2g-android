package vip.yazilim.p2g.android.data.p2g

import java.io.Serializable

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class UserDevice(
    var id: String,
    var userId: String,
    var platform: String,
    var deviceName: String,
    var deviceType: String,
    var activeFlag: Boolean
) : Serializable
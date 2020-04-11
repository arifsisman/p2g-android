package vip.yazilim.p2g.android.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Parcelize
data class UserDevice(
    var id: String,
    var userId: String,
    var deviceName: String,
    var deviceType: String,
    var activeFlag: Boolean
) : Parcelable
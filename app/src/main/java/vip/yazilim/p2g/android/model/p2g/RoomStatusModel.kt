package vip.yazilim.p2g.android.model.p2g

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import vip.yazilim.p2g.android.constant.enums.RoomStatus

/**
 * @author mustafaarifsisman - 12.04.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Parcelize
data class RoomStatusModel(
    var roomStatus: RoomStatus,
    var reason: String
) : Parcelable

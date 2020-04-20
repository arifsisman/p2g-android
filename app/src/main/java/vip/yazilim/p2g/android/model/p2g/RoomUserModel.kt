package vip.yazilim.p2g.android.model.p2g

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import vip.yazilim.p2g.android.entity.Room
import vip.yazilim.p2g.android.entity.RoomUser
import vip.yazilim.p2g.android.entity.User

/**
 * @author mustafaarifsisman - 07.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Parcelize
data class RoomUserModel(
    var room: Room?,
    var user: User?,
    var roomUser: RoomUser?
) : Parcelable
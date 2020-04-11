package vip.yazilim.p2g.android.model.p2g

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import vip.yazilim.p2g.android.entity.Room
import vip.yazilim.p2g.android.entity.Song
import vip.yazilim.p2g.android.entity.User

/**
 * @author mustafaarifsisman - 17.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Parcelize
data class RoomModel(
    var room: Room,
    var owner: User?,
    var song: Song?,
    var userCount: Int
) : Parcelable

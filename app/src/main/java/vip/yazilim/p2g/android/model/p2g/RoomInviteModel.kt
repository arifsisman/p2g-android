package vip.yazilim.p2g.android.model.p2g

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import vip.yazilim.p2g.android.entity.RoomInvite
import vip.yazilim.p2g.android.entity.User

/**
 * @author mustafaarifsisman - 02.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Parcelize
data class RoomInviteModel(
    var roomInvite: RoomInvite,
    var roomModel: RoomModel,
    var inviter: User
) : Parcelable
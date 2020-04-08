package vip.yazilim.p2g.android.model.p2g

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import vip.yazilim.p2g.android.entity.FriendRequest

/**
 * @author mustafaarifsisman - 02.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Parcelize
data class FriendRequestModel(
    var friendRequest: FriendRequest,
    var friendRequestUserModel: UserModel
) : Parcelable
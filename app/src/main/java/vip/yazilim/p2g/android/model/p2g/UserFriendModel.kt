package vip.yazilim.p2g.android.model.p2g

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author mustafaarifsisman - 09.04.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Parcelize
data class UserFriendModel(
    var requestModels: List<FriendRequestModel>,
    var friendModels: List<UserModel>
) : Parcelable
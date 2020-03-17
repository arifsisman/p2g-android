package vip.yazilim.p2g.android.model.p2g

import android.os.Parcel
import android.os.Parcelable
import vip.yazilim.p2g.android.entity.FriendRequest

/**
 * @author mustafaarifsisman - 02.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class FriendRequestModel(
    var friendRequest: FriendRequest?,
    var friendRequestUserModel: UserModel?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(FriendRequest::class.java.classLoader),
        parcel.readParcelable(UserModel::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(friendRequest, flags)
        parcel.writeParcelable(friendRequestUserModel, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FriendRequestModel> {
        override fun createFromParcel(parcel: Parcel): FriendRequestModel {
            return FriendRequestModel(parcel)
        }

        override fun newArray(size: Int): Array<FriendRequestModel?> {
            return arrayOfNulls(size)
        }
    }
}
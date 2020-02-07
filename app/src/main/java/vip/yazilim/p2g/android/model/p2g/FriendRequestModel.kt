package vip.yazilim.p2g.android.model.p2g

import android.os.Parcel
import android.os.Parcelable

/**
 * @author mustafaarifsisman - 02.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class FriendRequestModel(
    var friendRequest: FriendRequest?,
    var friendRequestUser: User?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(FriendRequest::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(friendRequest, flags)
        parcel.writeParcelable(friendRequestUser, flags)
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
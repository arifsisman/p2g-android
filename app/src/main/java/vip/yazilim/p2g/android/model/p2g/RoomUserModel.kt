package vip.yazilim.p2g.android.model.p2g

import android.os.Parcel
import android.os.Parcelable

/**
 * @author mustafaarifsisman - 07.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class RoomUserModel(
    var user: User?,
    var roomUser: RoomUser?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(RoomUser::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(user, flags)
        parcel.writeParcelable(roomUser, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RoomUserModel> {
        override fun createFromParcel(parcel: Parcel): RoomUserModel {
            return RoomUserModel(parcel)
        }

        override fun newArray(size: Int): Array<RoomUserModel?> {
            return arrayOfNulls(size)
        }
    }
}
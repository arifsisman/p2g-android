package vip.yazilim.p2g.android.model.p2g

import android.os.Parcel
import android.os.Parcelable

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class UserModel(
    var user: User? = null,
    var room: Room? = null,
    var roomUser: RoomUser? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(Room::class.java.classLoader),
        parcel.readParcelable(RoomUser::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(user, flags)
        parcel.writeParcelable(room, flags)
        parcel.writeParcelable(roomUser, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserModel> {
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel)
        }

        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }
}

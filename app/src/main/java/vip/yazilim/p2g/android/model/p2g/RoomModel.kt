package vip.yazilim.p2g.android.model.p2g

import android.os.Parcel
import android.os.Parcelable


/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class RoomModel(
    var room: Room?,
    var owner: User?,
    var userList: List<User>?,
    var roomUserList: List<RoomUser>?,
    var songList: List<Song>?,
    var invitedUserList: List<User>?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Room::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.createTypedArrayList(User),
        parcel.createTypedArrayList(RoomUser),
        parcel.createTypedArrayList(Song),
        parcel.createTypedArrayList(User)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(room, flags)
        parcel.writeParcelable(owner, flags)
        parcel.writeTypedList(userList)
        parcel.writeTypedList(roomUserList)
        parcel.writeTypedList(songList)
        parcel.writeTypedList(invitedUserList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RoomModel> {
        override fun createFromParcel(parcel: Parcel): RoomModel {
            return RoomModel(parcel)
        }

        override fun newArray(size: Int): Array<RoomModel?> {
            return arrayOfNulls(size)
        }
    }
}

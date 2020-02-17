package vip.yazilim.p2g.android.model.p2g

import android.os.Parcel
import android.os.Parcelable

/**
 * @author mustafaarifsisman - 17.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class RoomModelSimplified(
    var room: Room?,
    var owner: User?,
    var song: Song?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Room::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(Song::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(room, flags)
        parcel.writeParcelable(owner, flags)
        parcel.writeParcelable(song, flags)
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

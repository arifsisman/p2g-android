package vip.yazilim.p2g.android.model.p2g

import android.os.Parcel
import android.os.Parcelable

/**
 * @author mustafaarifsisman - 02.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class RoomInviteModel(
    var roomInvite: RoomInvite?,
    var roomModel: RoomModel?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(RoomInvite::class.java.classLoader),
        parcel.readParcelable(RoomModel::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(roomInvite, flags)
        parcel.writeParcelable(roomModel, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RoomInviteModel> {
        override fun createFromParcel(parcel: Parcel): RoomInviteModel {
            return RoomInviteModel(parcel)
        }

        override fun newArray(size: Int): Array<RoomInviteModel?> {
            return arrayOfNulls(size)
        }
    }
}
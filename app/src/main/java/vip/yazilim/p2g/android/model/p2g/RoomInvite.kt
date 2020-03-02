package vip.yazilim.p2g.android.model.p2g

import android.os.Parcel
import android.os.Parcelable
import org.threeten.bp.LocalDateTime

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class RoomInvite(
    var id: Long,
    var roomId: Long,
    var inviterId: String?,
    var receiverId: String?,
    var invitationDate: LocalDateTime?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readSerializable() as? LocalDateTime
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(roomId)
        parcel.writeString(inviterId)
        parcel.writeString(receiverId)
        parcel.writeSerializable(invitationDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RoomInvite> {
        override fun createFromParcel(parcel: Parcel): RoomInvite {
            return RoomInvite(parcel)
        }

        override fun newArray(size: Int): Array<RoomInvite?> {
            return arrayOfNulls(size)
        }
    }
}
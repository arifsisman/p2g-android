package vip.yazilim.p2g.android.model.p2g

import android.os.Parcel
import android.os.Parcelable
import org.threeten.bp.LocalDateTime

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class FriendRequest(
    var id: Long,
    var senderId: String?,
    var receiverId: String?,
    var requestStatus: String?,
    var requestDate: LocalDateTime?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readSerializable() as? LocalDateTime
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(senderId)
        parcel.writeString(receiverId)
        parcel.writeString(requestStatus)
        parcel.writeSerializable(requestDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FriendRequest> {
        override fun createFromParcel(parcel: Parcel): FriendRequest {
            return FriendRequest(parcel)
        }

        override fun newArray(size: Int): Array<FriendRequest?> {
            return arrayOfNulls(size)
        }
    }
}
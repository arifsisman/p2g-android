package vip.yazilim.p2g.android.model.websocket

import android.os.Parcel
import android.os.Parcelable
import org.threeten.bp.LocalDateTime

/**
 * @author mustafaarifsisman - 24.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class ChatMessage(
    var userId: String?,
    var userName: String?,
    var roomId: Long,
    var message: String?,
    var timestamp: LocalDateTime?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readSerializable() as LocalDateTime
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(userName)
        parcel.writeLong(roomId)
        parcel.writeString(message)
        parcel.writeSerializable(timestamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatMessage> {
        override fun createFromParcel(parcel: Parcel): ChatMessage {
            return ChatMessage(parcel)
        }

        override fun newArray(size: Int): Array<ChatMessage?> {
            return arrayOfNulls(size)
        }
    }
}



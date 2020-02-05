package vip.yazilim.p2g.android.model.p2g

import android.os.Parcel
import android.os.Parcelable
import org.threeten.bp.LocalDateTime

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class Room(
    var id: Long,
    var name: String?,
    var ownerId: String?,
    var creationDate: LocalDateTime,
    var privateFlag: Boolean,
    var password: String?,
    var maxUsers: Int,
    var usersAllowedQueueFlag: Boolean,
    var usersAllowedControlFlag: Boolean,
    var showRoomActivityFlag: Boolean,
    var activeFlag: Boolean,
    var countryCode: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readSerializable() as LocalDateTime,
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(ownerId)
        parcel.writeSerializable(creationDate)
        parcel.writeByte(if (privateFlag) 1 else 0)
        parcel.writeString(password)
        parcel.writeInt(maxUsers)
        parcel.writeByte(if (usersAllowedQueueFlag) 1 else 0)
        parcel.writeByte(if (usersAllowedControlFlag) 1 else 0)
        parcel.writeByte(if (showRoomActivityFlag) 1 else 0)
        parcel.writeByte(if (activeFlag) 1 else 0)
        parcel.writeString(countryCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Room> {
        override fun createFromParcel(parcel: Parcel): Room {
            return Room(parcel)
        }

        override fun newArray(size: Int): Array<Room?> {
            return arrayOfNulls(size)
        }
    }
}
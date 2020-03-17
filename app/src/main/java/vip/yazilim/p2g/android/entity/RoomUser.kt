package vip.yazilim.p2g.android.entity

import android.os.Parcel
import android.os.Parcelable
import com.stfalcon.chatkit.commons.models.IUser
import org.threeten.bp.LocalDateTime

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class RoomUser(
    var id: Long,
    var roomId: Long,
    var userId: String?,
    var userName: String?,
    var role: String?,
    var joinDate: LocalDateTime?,
    var activeFlag: Boolean
) : Parcelable, IUser {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readSerializable() as? LocalDateTime,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(roomId)
        parcel.writeString(userId)
        parcel.writeString(userName)
        parcel.writeString(role)
        parcel.writeSerializable(joinDate)
        parcel.writeByte(if (activeFlag) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RoomUser> {
        override fun createFromParcel(parcel: Parcel): RoomUser {
            return RoomUser(parcel)
        }

        override fun newArray(size: Int): Array<RoomUser?> {
            return arrayOfNulls(size)
        }
    }

    override fun getId(): String? {
        return this.userId
    }

    override fun getName(): String? {
        return this.userName
    }

    override fun getAvatar(): String? {
        return null
    }
}
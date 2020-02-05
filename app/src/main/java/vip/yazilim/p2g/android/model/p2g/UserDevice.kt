package vip.yazilim.p2g.android.model.p2g

import android.os.Parcel
import android.os.Parcelable

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class UserDevice(
    var id: String?,
    var userId: String?,
    var platform: String?,
    var deviceName: String?,
    var deviceType: String?,
    var activeFlag: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userId)
        parcel.writeString(platform)
        parcel.writeString(deviceName)
        parcel.writeString(deviceType)
        parcel.writeByte(if (activeFlag) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserDevice> {
        override fun createFromParcel(parcel: Parcel): UserDevice {
            return UserDevice(parcel)
        }

        override fun newArray(size: Int): Array<UserDevice?> {
            return arrayOfNulls(size)
        }
    }
}
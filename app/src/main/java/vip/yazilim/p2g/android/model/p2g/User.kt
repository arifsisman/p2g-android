package vip.yazilim.p2g.android.model.p2g

import android.os.Parcel
import android.os.Parcelable
import org.threeten.bp.LocalDateTime

/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class User(
    var id: String? = "",
    var name: String? = "",
    var email: String? = "",
    var role: String? = "",
    var onlineStatus: String? = "",
    var countryCode: String? = "",
    var imageUrl: String? = "",
    var anthem: String? = "",
    var spotifyProductType: String? = "",
    var showActivityFlag: Boolean = false,
    var showFriendsFlag: Boolean = false,
    var creationDate: LocalDateTime? = LocalDateTime.now()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readSerializable() as? LocalDateTime
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(role)
        parcel.writeString(onlineStatus)
        parcel.writeString(countryCode)
        parcel.writeString(imageUrl)
        parcel.writeString(anthem)
        parcel.writeString(spotifyProductType)
        parcel.writeByte(if (showActivityFlag) 1 else 0)
        parcel.writeByte(if (showFriendsFlag) 1 else 0)
        parcel.writeSerializable(creationDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
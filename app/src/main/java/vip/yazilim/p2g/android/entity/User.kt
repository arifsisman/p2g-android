package vip.yazilim.p2g.android.entity

import android.os.Parcel
import android.os.Parcelable
import org.threeten.bp.LocalDateTime
import vip.yazilim.p2g.android.util.helper.TimeHelper

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
    var anthemSongId: String? = "",
    var creationDate: LocalDateTime? = TimeHelper.getLocalDateTimeZonedUTC()
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
        parcel.writeString(anthemSongId)
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
package vip.yazilim.p2g.android.model.spotify

import android.os.Parcel
import android.os.Parcelable

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class TokenModel(
    val access_token: String?,
    val token_type: String?,
    val expires_in: Int,
    val refresh_token: String?,
    val scope: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(access_token)
        parcel.writeString(token_type)
        parcel.writeInt(expires_in)
        parcel.writeString(refresh_token)
        parcel.writeString(scope)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TokenModel> {
        override fun createFromParcel(parcel: Parcel): TokenModel {
            return TokenModel(parcel)
        }

        override fun newArray(size: Int): Array<TokenModel?> {
            return arrayOfNulls(size)
        }
    }
}
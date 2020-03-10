package vip.yazilim.p2g.android.model.p2g

import android.os.Parcel
import android.os.Parcelable
import vip.yazilim.p2g.android.entity.Song

/**
 * @author mustafaarifsisman - 17.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class FriendModel(var userModel: UserModel?, var song: Song?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(UserModel::class.java.classLoader),
        parcel.readParcelable(Song::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(userModel, flags)
        parcel.writeParcelable(song, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FriendModel> {
        override fun createFromParcel(parcel: Parcel): FriendModel {
            return FriendModel(parcel)
        }

        override fun newArray(size: Int): Array<FriendModel?> {
            return arrayOfNulls(size)
        }
    }
}
package vip.yazilim.p2g.android.model.p2g

import android.os.Parcel
import android.os.Parcelable
import vip.yazilim.p2g.android.constant.enums.SearchType

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class SearchModel(
    var type: SearchType,
    var name: String?,
    var artistNames: ArrayList<String>?,
    var albumName: String?,
    var id: String?,
    var uri: String?,
    var durationMs: Int,
    var imageUrl: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readSerializable() as SearchType,
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(type)
        parcel.writeString(name)
        parcel.writeStringList(artistNames)
        parcel.writeString(albumName)
        parcel.writeString(id)
        parcel.writeString(uri)
        parcel.writeInt(durationMs)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchModel> {
        override fun createFromParcel(parcel: Parcel): SearchModel {
            return SearchModel(parcel)
        }

        override fun newArray(size: Int): Array<SearchModel?> {
            return arrayOfNulls(size)
        }
    }
}

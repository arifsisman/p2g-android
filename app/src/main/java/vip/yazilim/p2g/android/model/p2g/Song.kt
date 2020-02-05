package vip.yazilim.p2g.android.model.p2g

import android.os.Parcel
import android.os.Parcelable
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class Song(
    var id: Long,
    var roomId: Long,
    var songId: String?,
    var songUri: String?,
    var songName: String?,
    var albumName: String?,
    var artistNames: ArrayList<String>?,
    var imageUrl: String?,
    var durationMs: Int,
    var songStatus: String?,
    var queuedTime: LocalDateTime,
    var playingTime: LocalDateTime,
    var currentMs: Int,
    var repeatFlag: Boolean,
    var votes: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readSerializable() as LocalDateTime,
        parcel.readSerializable() as LocalDateTime,
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(roomId)
        parcel.writeString(songId)
        parcel.writeString(songUri)
        parcel.writeString(songName)
        parcel.writeString(albumName)
        parcel.writeStringList(artistNames)
        parcel.writeString(imageUrl)
        parcel.writeInt(durationMs)
        parcel.writeString(songStatus)
        parcel.writeSerializable(queuedTime)
        parcel.writeSerializable(playingTime)
        parcel.writeInt(currentMs)
        parcel.writeByte(if (repeatFlag) 1 else 0)
        parcel.writeInt(votes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }
}
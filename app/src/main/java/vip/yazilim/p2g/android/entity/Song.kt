package vip.yazilim.p2g.android.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDateTime

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Parcelize
data class Song(
    var id: Long,
    var roomId: Long,
    var songId: String,
    var songName: String,
    var albumName: String,
    var artistNames: ArrayList<String>,
    var imageUrl: String?,
    var durationMs: Int,
    var songStatus: String,
    var queuedTime: LocalDateTime,
    var playingTime: LocalDateTime?,
    var currentMs: Int,
    var repeatFlag: Boolean,
    var votes: Int
) : Parcelable, Cloneable {
    public override fun clone(): Any {
        return Song(
            this.id,
            this.roomId,
            this.songId,
            this.songName,
            this.albumName,
            this.artistNames,
            this.imageUrl,
            this.durationMs,
            this.songStatus,
            this.queuedTime,
            this.playingTime,
            this.currentMs,
            this.repeatFlag,
            this.votes
        )
    }
}
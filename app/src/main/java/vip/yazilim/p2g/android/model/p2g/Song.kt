package vip.yazilim.p2g.android.model.p2g

import org.threeten.bp.LocalDateTime
import java.io.Serializable

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class Song(
    var id: Long,
    var roomId: Long,
    var songId: String,
    var songUri: String,
    var songName: String,
    var albumName: String,
    var artistNames: List<String>,
    var imageUrl: String,
    var durationMs: Int,
    var songStatus: String,
    var queuedTime: LocalDateTime,
    var playingTime: LocalDateTime,
    var currentMs: Int,
    var repeatFlag: Boolean,
    var votes: Int
) : Serializable
package vip.yazilim.p2g.android.util.helper

import android.view.View
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.model.p2g.Song

/**
 * @author mustafaarifsisman - 11.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomHelper {
    companion object {
        fun getRoomSongStatus(view: View, songList: List<Song>?): String {
            var result = ""
            songList?.forEach {
                when (it.songStatus) {
                    SongStatus.PLAYING.songStatus -> {
                        result =
                            "${view.resources.getString(R.string.placeholder_room_now_playing_song)} ${it.songName}" + getArtistsPlaceholder(
                                it.artistNames, " • "
                            )
                        return@forEach
                    }
                    SongStatus.PAUSED.songStatus -> {
                        result =
                            "${view.resources.getString(R.string.placeholder_room_paused_song)} ${it.songName}" + getArtistsPlaceholder(
                                it.artistNames, " • "
                            )
                        return@forEach
                    }
                    SongStatus.NEXT.songStatus -> {
                        result =
                            "${view.resources.getString(R.string.placeholder_room_next_song)} ${it.songName}" + getArtistsPlaceholder(
                                it.artistNames, " • "
                            )
                        return@forEach
                    }
                    else -> {
                        result = view.resources.getString(R.string.placeholder_room_song_not_found)
                        return@forEach
                    }
                }
            }
            return result
        }

        fun getRoomSongStatus(view: View, song: Song?): String {
            return when (song?.songStatus) {
                SongStatus.PLAYING.songStatus -> {
                    "${view.resources.getString(R.string.placeholder_room_now_playing_song)} ${song.songName}" + getArtistsPlaceholder(
                        song.artistNames, " • "
                    )
                }
                SongStatus.PAUSED.songStatus -> {
                    "${view.resources.getString(R.string.placeholder_room_paused_song)} ${song.songName}" + getArtistsPlaceholder(
                        song.artistNames, " • "
                    )
                }
                SongStatus.NEXT.songStatus -> {
                    "${view.resources.getString(R.string.placeholder_room_next_song)} ${song.songName}" + getArtistsPlaceholder(
                        song.artistNames, " • "
                    )
                }
                else -> {
                    view.resources.getString(R.string.placeholder_room_song_not_found)
                }
            }
        }

        fun getArtistsPlaceholder(artists: ArrayList<String>?, delimiter: String): String {
            return if (artists.isNullOrEmpty()) {
                ""
            } else {
                delimiter + artists
                    .toString()
                    .replace("[", "")
                    .replace("]", "")
                    .trim()
            }
        }

        fun getSongCurrentMs(song: Song): Long {
            val maxMs = song.durationMs.toLong()
            return when (song.songStatus) {
                SongStatus.PLAYING.songStatus -> {
                    val passed =
                        Duration.between(song.playingTime, LocalDateTime.now()).toMillis()
                    if (passed > maxMs) {
                        0
                    } else {
                        passed
                    }
                }
                SongStatus.PAUSED.songStatus -> {
                    song.currentMs.toLong()
                }
                else -> {
                    0
                }
            }
        }
    }
}
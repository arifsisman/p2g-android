package vip.yazilim.p2g.android.util.helper

import android.view.View
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.entity.Song

/**
 * @author mustafaarifsisman - 11.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomHelper {
    companion object {
        fun getRoomSongStatus(view: View, song: Song?): String {
            return when (song?.songStatus) {
                SongStatus.PLAYING.songStatus -> {
                    "• ${view.resources.getString(R.string.placeholder_room_now_playing_song)} ${song.songName}" + getArtistsPlaceholder(
                        song.artistNames, " • "
                    )
                }
                SongStatus.PAUSED.songStatus -> {
                    "• ${view.resources.getString(R.string.placeholder_room_paused_song)} ${song.songName}" + getArtistsPlaceholder(
                        song.artistNames, " • "
                    )
                }
                SongStatus.NEXT.songStatus -> {
                    "• ${view.resources.getString(R.string.placeholder_room_next_song)} ${song.songName}" + getArtistsPlaceholder(
                        song.artistNames, " • "
                    )
                }
                else -> {
                    "• ${view.resources.getString(R.string.placeholder_room_song_not_found)}"
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
    }
}
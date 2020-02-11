package vip.yazilim.p2g.android.util.helper

import android.view.View
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
                            "${view.resources.getString(R.string.placeholder_room_now_playing_song)} ${it.songName} - " + getArtistsPlaceholder(it.artistNames)
                        return@forEach
                    }
                    SongStatus.PAUSED.songStatus -> {
                        result =
                            "${view.resources.getString(R.string.placeholder_room_paused_song)} ${it.songName} - " + getArtistsPlaceholder(it.artistNames)
                        return@forEach
                    }
                    SongStatus.NEXT.songStatus -> {
                        result =
                            "${view.resources.getString(R.string.placeholder_room_next_song)} ${it.songName} - " + getArtistsPlaceholder(it.artistNames)
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

        private fun getArtistsPlaceholder(artists: ArrayList<String>?): String {
            return artists
                .toString()
                .replace("[", "")
                .replace("]", "")
                .trim()
        }
    }
}
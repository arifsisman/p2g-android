package vip.yazilim.p2g.android.ui.room

import androidx.lifecycle.MutableLiveData
import org.threeten.bp.Duration
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.api.Api.queue
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.entity.Song
import vip.yazilim.p2g.android.model.p2g.ChatMessage
import vip.yazilim.p2g.android.model.p2g.RoomUserModel
import vip.yazilim.p2g.android.ui.ViewModelBase
import vip.yazilim.p2g.android.util.helper.TimeHelper.Companion.getLocalDateTimeZonedUTC

/**
 * @author mustafaarifsisman - 20.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomViewModel : ViewModelBase() {
    val songCurrentMs = MutableLiveData<Int>()
    val isPlaying = MutableLiveData(false)
    val isSeeking = MutableLiveData(false)

    val songList = MutableLiveData<MutableList<Song>>()
    val playerSong = MutableLiveData<Song>()

    val roomUserModelList = MutableLiveData<MutableList<RoomUserModel>>()
    val roomUserModel = MutableLiveData<RoomUserModel>()
    val roomUserRole = MutableLiveData<String>()
    val newMessage = MutableLiveData<ChatMessage>()

    var messages: MutableList<ChatMessage> = mutableListOf()

    fun loadSongs(roomId: Long) {
        onViewLoading.postValue(true)

        Api.client.getRoomSongs(roomId).queue(onSuccess = {
            onViewLoading.postValue(false)

            songList.postValue(it)
            if (it.isNullOrEmpty()) {
                playerSong.postValue(null)
            } else {
                playerSong.postValue(it[0])
            }
        }, onFailure = {
            onViewLoading.postValue(false)
            onMessageError.postValue(it)
        })
    }

    fun loadRoomUsers(roomId: Long) {
        onViewLoading.postValue(true)

        Api.client.getRoomUserModels(roomId).queue(onSuccess = { roomUsers ->
            onViewLoading.postValue(false)

            roomUserModelList.postValue(roomUsers)

            roomUsers.forEach {
                if (it.user?.id == roomUserModel.value?.user?.id) {
                    roomUserModel.postValue(it)
                    roomUserRole.postValue(it.roomUser?.roomRole)
                }
            }
        }, onFailure = {
            onViewLoading.postValue(false)
            onMessageError.postValue(it)
        })
    }

    fun loadRoomUserMe() {
        Api.client.getRoomUserModelMe().queue(onSuccess = {
            roomUserModel.postValue(it)
            roomUserRole.postValue(it.roomUser?.roomRole)
        })
    }

    companion object {
        fun getCurrentSongMs(song: Song?): Int {
            if (song == null)
                return 0

            when (song.songStatus) {
                SongStatus.PAUSED.songStatus -> {
                    return if (song.currentMs > song.durationMs) song.durationMs else song.currentMs
                }
                SongStatus.PLAYING.songStatus -> {
                    val passed =
                        Duration
                            .between(song.playingTime, getLocalDateTimeZonedUTC())
                            .toMillis()
                            .toInt()
                    return when {
                        passed > song.durationMs -> {
                            song.durationMs
                        }
                        song.currentMs > passed -> {
                            song.currentMs
                        }
                        else -> {
                            passed
                        }
                    }
                }
                else -> {
                    return 0
                }
            }
        }
    }
}
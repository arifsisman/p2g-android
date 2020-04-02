package vip.yazilim.p2g.android.ui.room

import androidx.lifecycle.MutableLiveData
import org.threeten.bp.Duration
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.entity.Song
import vip.yazilim.p2g.android.entity.User
import vip.yazilim.p2g.android.model.p2g.ChatMessage
import vip.yazilim.p2g.android.model.p2g.RoomUserModel
import vip.yazilim.p2g.android.ui.ViewModelBase
import vip.yazilim.p2g.android.util.helper.TimeHelper.Companion.getLocalDateTimeZonedUTC
import vip.yazilim.p2g.android.util.refrofit.Singleton

/**
 * @author mustafaarifsisman - 20.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomViewModel : ViewModelBase() {
    val songList = MutableLiveData<MutableList<Song>>()
    val playerSong = MutableLiveData<Song>()
    val roomUserModelList = MutableLiveData<MutableList<RoomUserModel>>()
    val roomUserModel = MutableLiveData<RoomUserModel>()
    val roomInviteUserList = MutableLiveData<MutableList<User>>()
    var messages: MutableList<ChatMessage> = mutableListOf()
    val newMessage: MutableLiveData<ChatMessage> = MutableLiveData<ChatMessage>()

    fun loadSongs(roomId: Long) {
        onViewLoading.postValue(true)

        request(
            Singleton.apiClient().getRoomSongs(roomId),
            object : Callback<MutableList<Song>> {
                override fun onError(msg: String) {
                    onViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: MutableList<Song>) {
                    onViewLoading.postValue(false)

                    songList.postValue(obj)
                    playerSong.postValue(getCurrentSong(obj))
                }
            })
    }

    fun loadRoomUsers(roomId: Long) {
        onViewLoading.postValue(true)

        request(
            Singleton.apiClient().getRoomUserModels(roomId),
            object : Callback<MutableList<RoomUserModel>> {
                override fun onError(msg: String) {
                    onViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: MutableList<RoomUserModel>) {
                    onViewLoading.postValue(false)

                    roomUserModelList.postValue(obj)

                    obj.forEach {
                        if (it.user?.id == roomUserModel.value?.user?.id) {
                            roomUserModel.postValue(it)
                        }
                    }

                }
            })
    }

    fun loadRoomUserMe() {
        request(Singleton.apiClient().getRoomUserModelMe(), object : Callback<RoomUserModel> {
            override fun onSuccess(obj: RoomUserModel) {
                roomUserModel.postValue(obj)
            }

            override fun onError(msg: String) {
            }
        })
    }

    fun loadRoomInviteUsers() {
        onViewLoading.postValue(true)

        request(
            Singleton.apiClient().getAllUsers(),
            object : Callback<MutableList<User>> {
                override fun onError(msg: String) {
                    onViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: MutableList<User>) {
                    onViewLoading.postValue(false)
                    roomInviteUserList.postValue(obj)
                }
            })
    }

    fun getCurrentSong(songList: MutableList<Song>): Song? {
        var playingSong: Song? = null
        var pausedSong: Song? = null
        var nextSong: Song? = null

        songList.forEach {
            when (it.songStatus) {
                SongStatus.PLAYING.songStatus -> {
                    playingSong = it
                }
                SongStatus.PAUSED.songStatus -> {
                    pausedSong = it
                }
                SongStatus.NEXT.songStatus -> {
                    if (nextSong == null) {
                        nextSong = it
                    }
                }
            }
        }

        return when {
            playingSong != null -> {
                playingSong
            }
            pausedSong != null -> {
                pausedSong
            }
            nextSong != null -> {
                nextSong
            }
            else -> {
                null
            }
        }
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
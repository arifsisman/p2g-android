package vip.yazilim.p2g.android.ui.room.roomqueue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.model.p2g.Song
import vip.yazilim.p2g.android.ui.ViewModelBase
import vip.yazilim.p2g.android.util.refrofit.Singleton

/**
 * @author mustafaarifsisman - 20.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomQueueViewModel : ViewModelBase() {
    private var _songOnPlayer = MutableLiveData<Song>()
    val songOnPlayer: LiveData<Song> = _songOnPlayer

    private val _songs = MutableLiveData<MutableList<Song>>()
    val songs: LiveData<MutableList<Song>> = _songs

    fun loadSongs(roomId: Long) {
        _isViewLoading.postValue(true)

        request(
            Singleton.apiClient().getRoomSongs(roomId),
            object : Callback<MutableList<Song>> {
                override fun onError(msg: String) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: MutableList<Song>) {
                    _isViewLoading.postValue(false)

                    if (obj.isEmpty()) {
                        _isEmptyList.postValue(true)
                    } else {
                        _songs.value = obj

                        obj.forEach {
                            when (it.songStatus) {
                                SongStatus.PLAYING.songStatus -> {
                                    _songOnPlayer.value = it
                                    return@forEach
                                }
                                SongStatus.PAUSED.songStatus -> {
                                    _songOnPlayer.value = it
                                    return@forEach
                                }
                                SongStatus.NEXT.songStatus -> {
                                    _songOnPlayer.value = it
                                    return@forEach
                                }
                            }
                        }
                    }
                }
            })
    }
}
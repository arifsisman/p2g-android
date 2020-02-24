package vip.yazilim.p2g.android.ui.room.roomqueue

import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.model.p2g.Song
import vip.yazilim.p2g.android.ui.ViewModelBase
import vip.yazilim.p2g.android.util.refrofit.Singleton

/**
 * @author mustafaarifsisman - 20.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomViewModel : ViewModelBase() {
    private val _songList = MutableLiveData<MutableList<Song>>()
    val songList: MutableLiveData<MutableList<Song>> = _songList

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
                        _songList.value = obj
                    }
                }
            })
    }
}
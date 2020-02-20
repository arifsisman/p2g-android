package vip.yazilim.p2g.android.ui.roomqueue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.model.p2g.Song
import vip.yazilim.p2g.android.ui.ViewModelBase

/**
 * @author mustafaarifsisman - 20.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomQueueViewModel : ViewModelBase() {
    private val _songs = MutableLiveData<MutableList<Song>>()
    val songs: LiveData<MutableList<Song>> = _songs

//    fun loadSongs() {
//        _isViewLoading.postValue(true)
//
//        request(
//            Singleton.apiClient().getSimplifiedRoomModels(),
//            object : Callback<MutableList<RoomModelSimplified>> {
//                override fun onError(msg: String) {
//                    _isViewLoading.postValue(false)
//                    _onMessageError.postValue(msg)
//                }
//
//                override fun onSuccess(obj: MutableList<RoomModelSimplified>) {
//                    _isViewLoading.postValue(false)
//
//                    if (obj.isEmpty()) {
//                        _isEmptyList.postValue(true)
//                    } else {
//                        _roomModels.value = obj
//                    }
//                }
//            })
//    }
}
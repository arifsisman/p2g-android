package vip.yazilim.p2g.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.p2gRequest
import vip.yazilim.p2g.android.model.p2g.RoomModelSimplified
import vip.yazilim.p2g.android.ui.ViewModelBase

/**
 * @author mustafaarifsisman - 04.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class HomeViewModel : ViewModelBase() {

    private val _roomModels = MutableLiveData<MutableList<RoomModelSimplified>>()
    val roomModels: LiveData<MutableList<RoomModelSimplified>> = _roomModels

    fun loadRooms() {
        _isViewLoading.postValue(true)

        p2gRequest(
            ApiClient.build().getSimplifiedRoomModels(),
            object : Callback<MutableList<RoomModelSimplified>> {
                override fun onError(msg: String) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: MutableList<RoomModelSimplified>) {
                    _isViewLoading.postValue(false)

                    if (obj.isEmpty()) {
                        _isEmptyList.postValue(true)
                    } else {
                        _roomModels.value = obj
                    }
                }
            })
    }

}
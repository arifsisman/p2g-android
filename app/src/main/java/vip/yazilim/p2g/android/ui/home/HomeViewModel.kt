package vip.yazilim.p2g.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.P2GRequest
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.ui.ViewModelBase


class HomeViewModel : ViewModelBase() {

    private val _roomModels = MutableLiveData<List<RoomModel>>()
    val roomModels: LiveData<List<RoomModel>> = _roomModels

    fun loadRooms() {
        _isViewLoading.postValue(true)

        P2GRequest.build(
            ApiClient.build().getRoomModels(),
            object : Callback<List<RoomModel>> {
                override fun onError(msg: String) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: List<RoomModel>) {
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
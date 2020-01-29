package vip.yazilim.p2g.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.Request
import vip.yazilim.p2g.android.model.p2g.RoomModel


class HomeViewModel : ViewModel() {

    private val _roomModels = MutableLiveData<List<RoomModel>>().apply { value = emptyList() }
    val roomModels: LiveData<List<RoomModel>> = _roomModels

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> = _isViewLoading

    private val _onMessageError = MutableLiveData<Any>()
    val onMessageError: LiveData<Any> = _onMessageError

    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean> = _isEmptyList

//    val searchString = MutableLiveData<String>()

    fun loadRooms() {
        _isViewLoading.postValue(true)

        Request.build(
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
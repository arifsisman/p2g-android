package vip.yazilim.p2g.android.ui.roominvites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.P2GRequest
import vip.yazilim.p2g.android.model.p2g.RoomInviteModel

/**
 * @author mustafaarifsisman - 31.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomInvitesViewModel : ViewModel() {

    private val _roomInviteModel = MutableLiveData<RoomInviteModel>()
    val roomInviteModel: LiveData<RoomInviteModel> = _roomInviteModel

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> = _isViewLoading

    private val _onMessageError = MutableLiveData<Any>()
    val onMessageError: LiveData<Any> = _onMessageError

    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean> = _isEmptyList

    fun loadRoomInviteModel() {
        _isViewLoading.postValue(true)

        P2GRequest.build(
            ApiClient.build().getRoomInviteModel(),
            object : Callback<RoomInviteModel> {
                override fun onError(msg: String) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: RoomInviteModel) {
                    _isViewLoading.postValue(false)

                    if (obj.roomInvites.isNullOrEmpty()) {
                        _isEmptyList.postValue(true)
                    } else {
                        _roomInviteModel.value = obj
                    }
                }
            })
    }
}
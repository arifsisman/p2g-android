package vip.yazilim.p2g.android.ui.roominvites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.P2GRequest
import vip.yazilim.p2g.android.model.p2g.RoomInviteModel
import vip.yazilim.p2g.android.ui.ViewModelBase

/**
 * @author mustafaarifsisman - 31.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomInvitesViewModel : ViewModelBase() {

    private val _roomInviteModel = MutableLiveData<MutableList<RoomInviteModel>>()
    val roomInviteModel: LiveData<MutableList<RoomInviteModel>> = _roomInviteModel

    fun loadRoomInviteModel() {
        _isViewLoading.postValue(true)

        P2GRequest.run {
            build(
                ApiClient.build().getRoomInviteModels(),
                object : Callback<MutableList<RoomInviteModel>> {
                    override fun onError(msg: String) {
                        _isViewLoading.postValue(false)
                        _onMessageError.postValue(msg)
                    }

                    override fun onSuccess(obj: MutableList<RoomInviteModel>) {
                        _isViewLoading.postValue(false)

                        if (obj.isNullOrEmpty()) {
                            _isEmptyList.postValue(true)
                        } else {
                            _roomInviteModel.value = obj
                        }
                    }
                })
        }
    }
}
package vip.yazilim.p2g.android.ui.main.invites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.model.p2g.RoomInviteModel
import vip.yazilim.p2g.android.ui.ViewModelBase
import vip.yazilim.p2g.android.util.refrofit.Singleton

/**
 * @author mustafaarifsisman - 31.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class InvitesViewModel : ViewModelBase() {

    private val _roomInviteModel = MutableLiveData<MutableList<RoomInviteModel>>()
    val roomInviteModel: LiveData<MutableList<RoomInviteModel>> = _roomInviteModel

    fun loadRoomInviteModel() {
        _isViewLoading.postValue(true)

        request(
            Singleton.apiClient().getRoomInviteModels(),
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
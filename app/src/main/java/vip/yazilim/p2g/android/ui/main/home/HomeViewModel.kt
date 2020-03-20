package vip.yazilim.p2g.android.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.ui.ViewModelBase
import vip.yazilim.p2g.android.util.refrofit.Singleton

/**
 * @author mustafaarifsisman - 04.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class HomeViewModel : ViewModelBase() {

    private val _roomModels = MutableLiveData<MutableList<RoomModel>>()
    val roomModels: LiveData<MutableList<RoomModel>> = _roomModels

    fun loadRooms() {
        _isViewLoading.postValue(true)

        request(
            Singleton.apiClient().getRoomModels(),
            object : Callback<MutableList<RoomModel>> {
                override fun onError(msg: String) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: MutableList<RoomModel>) {
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
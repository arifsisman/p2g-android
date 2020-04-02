package vip.yazilim.p2g.android.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.ui.ViewModelBase
import vip.yazilim.p2g.android.util.refrofit.Singleton

/**
 * @author mustafaarifsisman - 11.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class UserViewModel : ViewModelBase() {
    private val _friendCounts = MutableLiveData<Int>()
    val friendCounts: LiveData<Int> = _friendCounts

    private val _roomModel = MutableLiveData<RoomModel>()
    val roomModel: LiveData<RoomModel> = _roomModel

    fun loadFriendsCount(userId: String) = request(
        Singleton.apiClient().getFriendsCounts(userId),
        object : Callback<Int> {
            override fun onError(msg: String) {
            }

            override fun onSuccess(obj: Int) {
                _friendCounts.postValue(obj)
            }
        })

    fun loadRoomModel(roomId: Long) = request(
        Singleton.apiClient().getRoomModel(roomId),
        object : Callback<RoomModel> {
            override fun onError(msg: String) {
            }

            override fun onSuccess(obj: RoomModel) {
                _roomModel.postValue(obj)
            }
        })

}
package vip.yazilim.p2g.android.ui.user

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
    val friendCounts = MutableLiveData<Int>()
    val roomModel = MutableLiveData<RoomModel>()

    fun loadFriendsCount(userId: String) = request(
        Singleton.apiClient().getFriendsCounts(userId),
        object : Callback<Int> {
            override fun onError(msg: String) {
            }

            override fun onSuccess(obj: Int) {
                friendCounts.postValue(obj)
            }
        })

    fun loadRoomModel(roomId: Long) = request(
        Singleton.apiClient().getRoomModel(roomId),
        object : Callback<RoomModel> {
            override fun onError(msg: String) {
            }

            override fun onSuccess(obj: RoomModel) {
                roomModel.postValue(obj)
            }
        })

}
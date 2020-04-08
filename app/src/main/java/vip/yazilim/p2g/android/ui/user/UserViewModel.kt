package vip.yazilim.p2g.android.ui.user

import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.api.Api.queue
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.ui.ViewModelBase

/**
 * @author mustafaarifsisman - 11.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class UserViewModel : ViewModelBase() {
    val friendCounts = MutableLiveData<Int>()
    val roomModel = MutableLiveData<RoomModel>()

    fun loadFriendsCount(userId: String) = Api.client.getFriendsCounts(userId).queue(
        object : Callback<Int> {
            override fun onError(msg: String) {
            }

            override fun onSuccess(obj: Int) {
                friendCounts.postValue(obj)
            }
        })

    fun loadRoomModel(roomId: Long) = Api.client.getRoomModel(roomId).queue(
        object : Callback<RoomModel> {
            override fun onError(msg: String) {
            }

            override fun onSuccess(obj: RoomModel) {
                roomModel.postValue(obj)
            }
        })

}
package vip.yazilim.p2g.android.ui.user

import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.api.Api.withCallback
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

    fun loadFriendsCount(userId: String) = Api.client.getFriendsCounts(userId).withCallback(
        object : Callback<Int> {
            override fun onError(msg: String) {
            }

            override fun onSuccess(obj: Int) {
                friendCounts.postValue(obj)
            }
        })

}
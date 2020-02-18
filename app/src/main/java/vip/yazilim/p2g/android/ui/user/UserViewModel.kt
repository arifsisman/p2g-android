package vip.yazilim.p2g.android.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.p2gRequest
import vip.yazilim.p2g.android.model.p2g.RoomModelSimplified
import vip.yazilim.p2g.android.ui.ViewModelBase
import vip.yazilim.p2g.android.util.refrofit.Singleton

/**
 * @author mustafaarifsisman - 11.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class UserViewModel : ViewModelBase() {
    private val _friendCounts = MutableLiveData<Int>()
    val friendCounts: LiveData<Int> = _friendCounts

    private val _roomModel = MutableLiveData<RoomModelSimplified>()
    val roomModel: LiveData<RoomModelSimplified> = _roomModel

    fun loadFriendsCount(userId: String) = p2gRequest(
        Singleton.apiClient().getFriendsCounts(userId),
        object : Callback<Int> {
            override fun onError(msg: String) {
            }

            override fun onSuccess(obj: Int) {
                _friendCounts.value = obj
            }
        })

    fun loadRoomModel(roomId: Long) = p2gRequest(
        Singleton.apiClient().getSimplifiedRoomModel(roomId),
        object : Callback<RoomModelSimplified> {
            override fun onError(msg: String) {
            }

            override fun onSuccess(obj: RoomModelSimplified) {
                _roomModel.value = obj
            }
        })

}
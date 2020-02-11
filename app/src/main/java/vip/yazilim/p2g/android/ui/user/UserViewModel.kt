package vip.yazilim.p2g.android.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.P2GRequest
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.ViewModelBase

/**
 * @author mustafaarifsisman - 11.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class UserViewModel : ViewModelBase() {
    private val _friends = MutableLiveData<MutableList<UserModel>>()
    val friends: LiveData<MutableList<UserModel>> = _friends

    private val _roomModel = MutableLiveData<RoomModel>()
    val roomModel: LiveData<RoomModel> = _roomModel

    fun loadFriendsCount(userId: String) {
        P2GRequest.build(
            ApiClient.build().getFriends(userId),
            object : Callback<MutableList<UserModel>> {
                override fun onError(msg: String) {
                }

                override fun onSuccess(obj: MutableList<UserModel>) {
                    _friends.value = obj
                }
            })
    }

    fun loadRoomModel(roomId: Long) {
        P2GRequest.build(
            ApiClient.build().getRoomModel(roomId),
            object : Callback<RoomModel> {
                override fun onError(msg: String) {
                }

                override fun onSuccess(obj: RoomModel) {
                    _roomModel.value = obj
                }
            })
    }
}
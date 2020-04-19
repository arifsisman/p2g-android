package vip.yazilim.p2g.android.ui.main

import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.api.Api.then
import vip.yazilim.p2g.android.model.p2g.RoomInviteModel
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.model.p2g.UserFriendModel
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.ViewModelBase

/**
 * @author mustafaarifsisman - 20.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class MainViewModel : ViewModelBase() {

    val roomModels = MutableLiveData<MutableList<RoomModel>>()
    val userFriendModel = MutableLiveData<UserFriendModel>()
    val roomInviteModel = MutableLiveData<MutableList<RoomInviteModel>>()
    val userModel = MutableLiveData<UserModel>()
    val friendCountsMe = MutableLiveData<Int>()
    val roomModel = MutableLiveData<RoomModel>()

    fun loadRooms() {
        onViewLoading.postValue(true)

        Api.client.getRoomModels() then { obj, msg ->
            obj?.let {
                onViewLoading.postValue(false)
                roomModels.postValue(obj)
            }
            msg?.let {
                onViewLoading.postValue(false)
                onMessageError.postValue(msg)
            }
        }
    }

    fun loadUserFriendModel() {
        onViewLoading.postValue(true)

        Api.client.getUserFriendModel() then { obj, msg ->
            obj?.let {
                onViewLoading.postValue(false)
                userFriendModel.postValue(obj)
            }
            msg?.let {
                onViewLoading.postValue(false)
                onMessageError.postValue(msg)
            }
        }
    }

    fun loadRoomInviteModel() {
        onViewLoading.postValue(true)

        Api.client.getRoomInviteModels() then { obj, msg ->
            obj?.let {
                onViewLoading.postValue(false)
                roomInviteModel.postValue(obj)
            }
            msg?.let {
                onViewLoading.postValue(false)
                onMessageError.postValue(msg)
            }
        }
    }

    fun loadUserModel() {
        onViewLoading.postValue(true)

        Api.client.getUserModelMe() then { obj, msg ->
            obj?.let {
                userModel.postValue(obj)
                onViewLoading.postValue(false)
            }
            msg?.let {
                onViewLoading.postValue(false)
                onMessageError.postValue(msg)
            }
        }
    }

    fun loadFriendsCountMe() = Api.client.getFriendsCounts() then { obj, _ ->
        obj?.let {
            friendCountsMe.postValue(obj)
        }
    }
}
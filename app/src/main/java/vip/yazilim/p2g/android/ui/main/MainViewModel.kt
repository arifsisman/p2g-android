package vip.yazilim.p2g.android.ui.main

import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.api.Api.queue
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

        Api.client.getRoomModels().queue(success = {
            onViewLoading.postValue(false)
            roomModels.postValue(it)
        }, failure = {
            onViewLoading.postValue(false)
            onMessageError.postValue(it)
        })
    }

    fun loadUserFriendModel() {
        onViewLoading.postValue(true)

        Api.client.getUserFriendModel().queue(success = {
            onViewLoading.postValue(false)
            userFriendModel.postValue(it)
        }, failure = {
            onViewLoading.postValue(false)
            onMessageError.postValue(it)
        })
    }

    fun loadRoomInviteModel() {
        onViewLoading.postValue(true)

        Api.client.getRoomInviteModels().queue(success = {
            onViewLoading.postValue(false)
            roomInviteModel.postValue(it)
        }, failure = {
            onViewLoading.postValue(false)
            onMessageError.postValue(it)
        })
    }

    fun loadUserModel() {
        onViewLoading.postValue(true)

        Api.client.getUserModelMe().queue(success = {
            userModel.postValue(it)
            onViewLoading.postValue(false)
        }, failure = {
            onViewLoading.postValue(false)
            onMessageError.postValue(it)
        })
    }

    fun loadFriendsCountMe() = Api.client.getFriendsCounts()
        .queue(success = { friendCountsMe.postValue(it) }, failure = {})
}
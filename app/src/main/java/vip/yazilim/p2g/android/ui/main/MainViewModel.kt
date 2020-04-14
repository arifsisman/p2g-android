package vip.yazilim.p2g.android.ui.main

import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.api.Api.withCallback
import vip.yazilim.p2g.android.api.generic.Callback
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

        Api.client?.getRoomModels()?.withCallback(
            object : Callback<MutableList<RoomModel>> {
                override fun onError(msg: String) {
                    onViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: MutableList<RoomModel>) {
                    onViewLoading.postValue(false)
                    roomModels.postValue(obj)
                }
            })
    }

    fun loadUserFriendModel() {
        onViewLoading.postValue(true)

        Api.client?.getUserFriendModel()?.withCallback(
            object : Callback<UserFriendModel> {
                override fun onError(msg: String) {
                    onViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: UserFriendModel) {
                    onViewLoading.postValue(false)
                    userFriendModel.postValue(obj)
                }
            })
    }

    fun loadRoomInviteModel() {
        onViewLoading.postValue(true)

        Api.client?.getRoomInviteModels()?.withCallback(
            object : Callback<MutableList<RoomInviteModel>> {
                override fun onError(msg: String) {
                    onViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: MutableList<RoomInviteModel>) {
                    onViewLoading.postValue(false)
                    roomInviteModel.postValue(obj)
                }
            })
    }

    fun loadUserModel() {
        onViewLoading.postValue(true)

        Api.client?.getUserModelMe()?.withCallback(
            object : Callback<UserModel> {
                override fun onError(msg: String) {
                    onViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: UserModel) {
                    userModel.postValue(obj)
                    onViewLoading.postValue(false)
                }
            })
    }

    fun loadFriendsCountMe() = Api.client?.getFriendsCounts()?.withCallback(
        object : Callback<Int> {
            override fun onError(msg: String) {
            }

            override fun onSuccess(obj: Int) {
                friendCountsMe.postValue(obj)
            }
        })
}
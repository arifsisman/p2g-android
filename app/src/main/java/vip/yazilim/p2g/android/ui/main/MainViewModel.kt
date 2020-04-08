package vip.yazilim.p2g.android.ui.main

import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.api.Api.queue
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.model.p2g.*
import vip.yazilim.p2g.android.ui.ViewModelBase

/**
 * @author mustafaarifsisman - 20.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class MainViewModel : ViewModelBase() {

    val roomModels = MutableLiveData<MutableList<RoomModel>>()
    val friendRequestModel = MutableLiveData<MutableList<Any>>()
    val roomInviteModel = MutableLiveData<MutableList<RoomInviteModel>>()
    val userModel = MutableLiveData<UserModel>()
    val friendCountsMe = MutableLiveData<Int>()
    val roomModel = MutableLiveData<RoomModel>()

    fun loadRooms() {
        onViewLoading.postValue(true)

        Api.client.getRoomModels().queue(
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

    fun loadFriendRequestModel() {
        onViewLoading.postValue(true)

        Api.client.getFriendRequestModels().queue(
            object : Callback<MutableList<FriendRequestModel>> {
                override fun onError(msg: String) {
                    onViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                @Suppress("UNCHECKED_CAST")
                override fun onSuccess(obj: MutableList<FriendRequestModel>) {
                    onViewLoading.postValue(false)
                    friendRequestModel.postValue(obj as MutableList<Any>)
                }
            })
    }

    fun loadFriends() {
        onViewLoading.postValue(true)

        Api.client.getFriendModels().queue(
            object : Callback<MutableList<FriendModel>> {
                override fun onError(msg: String) {
                    onViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                @Suppress("UNCHECKED_CAST")
                override fun onSuccess(obj: MutableList<FriendModel>) {
                    onViewLoading.postValue(false)
                    friendRequestModel.postValue(obj as MutableList<Any>)
                }
            })
    }

    fun loadRoomInviteModel() {
        onViewLoading.postValue(true)

        Api.client.getRoomInviteModels().queue(
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

        Api.client.getUserModelMe().queue(
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

    fun loadFriendsCountMe() = Api.client.getFriendsCounts().queue(
        object : Callback<Int> {
            override fun onError(msg: String) {
            }

            override fun onSuccess(obj: Int) {
                friendCountsMe.postValue(obj)
            }
        })
}
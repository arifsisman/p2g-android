package vip.yazilim.p2g.android.ui.main

import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.model.p2g.*
import vip.yazilim.p2g.android.ui.ViewModelBase
import vip.yazilim.p2g.android.util.refrofit.Singleton

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

        request(
            Singleton.apiClient().getRoomModels(),
            object : Callback<MutableList<RoomModel>> {
                override fun onError(msg: String) {
                    onViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: MutableList<RoomModel>) {
                    onViewLoading.postValue(false)

                    if (obj.isNotEmpty()) {
                        roomModels.postValue(obj)
                        onEmptyList.postValue(false)
                    } else {
                        onEmptyList.postValue(true)
                    }
                }
            })
    }

    fun loadFriendRequestModel() {
        onViewLoading.postValue(true)

        request(
            Singleton.apiClient().getFriendRequestModels(),
            object : Callback<MutableList<FriendRequestModel>> {
                override fun onError(msg: String) {
                    onViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                @Suppress("UNCHECKED_CAST")
                override fun onSuccess(obj: MutableList<FriendRequestModel>) {
                    if (obj.isNullOrEmpty() && friendRequestModel.value.isNullOrEmpty()) {
                        onEmptyList.postValue(true)
                        onViewLoading.postValue(false)
                    } else {
                        onViewLoading.postValue(false)
                        onEmptyList.postValue(false)
                        onViewLoading.postValue(false)
                        friendRequestModel.postValue(obj as MutableList<Any>)
                    }
                }
            })
    }

    fun loadFriends() {
        onViewLoading.postValue(true)

        request(
            Singleton.apiClient().getFriendModels(),
            object : Callback<MutableList<FriendModel>> {
                override fun onError(msg: String) {
                    onViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                @Suppress("UNCHECKED_CAST")
                override fun onSuccess(obj: MutableList<FriendModel>) {
                    if (obj.isNullOrEmpty() && friendRequestModel.value.isNullOrEmpty()) {
                        onEmptyList.postValue(true)
                        onViewLoading.postValue(false)
                    } else {
                        onViewLoading.postValue(false)
                        onEmptyList.postValue(false)
                        onViewLoading.postValue(false)
                        friendRequestModel.postValue(obj as MutableList<Any>)
                    }
                }
            })
    }

    fun loadRoomInviteModel() {
        onViewLoading.postValue(true)

        request(
            Singleton.apiClient().getRoomInviteModels(),
            object : Callback<MutableList<RoomInviteModel>> {
                override fun onError(msg: String) {
                    onViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: MutableList<RoomInviteModel>) {
                    onViewLoading.postValue(false)

                    if (obj.isNullOrEmpty()) {
                        onEmptyList.postValue(true)
                    } else {
                        onEmptyList.postValue(false)
                        roomInviteModel.postValue(obj)
                    }
                }
            })
    }

    fun loadUserModel() {
        onViewLoading.postValue(true)

        request(
            Singleton.apiClient().getUserModelMe(),
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

    fun loadFriendsCountMe() = request(
        Singleton.apiClient().getFriendsCounts(),
        object : Callback<Int> {
            override fun onError(msg: String) {
            }

            override fun onSuccess(obj: Int) {
                friendCountsMe.postValue(obj)
            }
        })
}
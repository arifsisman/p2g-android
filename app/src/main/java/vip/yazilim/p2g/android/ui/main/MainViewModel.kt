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
        isViewLoading.postValue(true)

        request(
            Singleton.apiClient().getRoomModels(),
            object : Callback<MutableList<RoomModel>> {
                override fun onError(msg: String) {
                    isViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: MutableList<RoomModel>) {
                    isViewLoading.postValue(false)

                    if (obj.isNotEmpty()) {
                        roomModels.postValue(obj)
                    }
                }
            })
    }

    fun loadFriendRequestModel() {
        isViewLoading.postValue(true)

        request(
            Singleton.apiClient().getFriendRequestModels(),
            object : Callback<MutableList<FriendRequestModel>> {
                override fun onError(msg: String) {
                    isViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                @Suppress("UNCHECKED_CAST")
                override fun onSuccess(obj: MutableList<FriendRequestModel>) {
                    isViewLoading.postValue(false)
                    friendRequestModel.postValue(obj as MutableList<Any>)
                }
            })
    }

    fun loadFriends() {
        isViewLoading.postValue(true)

        request(
            Singleton.apiClient().getFriendModels(),
            object : Callback<MutableList<FriendModel>> {
                override fun onError(msg: String) {
                    isViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                @Suppress("UNCHECKED_CAST")
                override fun onSuccess(obj: MutableList<FriendModel>) {
                    isViewLoading.postValue(false)
                    friendRequestModel.postValue(obj as MutableList<Any>)
                }
            })
    }

    fun loadRoomInviteModel() {
        isViewLoading.postValue(true)

        request(
            Singleton.apiClient().getRoomInviteModels(),
            object : Callback<MutableList<RoomInviteModel>> {
                override fun onError(msg: String) {
                    isViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: MutableList<RoomInviteModel>) {
                    isViewLoading.postValue(false)

                    if (!obj.isNullOrEmpty()) {
                        roomInviteModel.postValue(obj)
                    }
                }
            })
    }

    fun loadUserModel() {
        isViewLoading.postValue(true)

        request(
            Singleton.apiClient().getUserModelMe(),
            object : Callback<UserModel> {
                override fun onError(msg: String) {
                    isViewLoading.postValue(false)
                    onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: UserModel) {
                    userModel.postValue(obj)
                    isViewLoading.postValue(false)
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
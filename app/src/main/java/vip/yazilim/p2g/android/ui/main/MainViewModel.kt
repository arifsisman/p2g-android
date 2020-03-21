package vip.yazilim.p2g.android.ui.main

import androidx.lifecycle.LiveData
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

    private val _roomModels = MutableLiveData<MutableList<RoomModel>>()
    val roomModels: LiveData<MutableList<RoomModel>> = _roomModels

    private val _friendRequestModel = MutableLiveData<MutableList<Any>>()
    val friendRequestModel: LiveData<MutableList<Any>> = _friendRequestModel

    private val _roomInviteModel = MutableLiveData<MutableList<RoomInviteModel>>()
    val roomInviteModel: LiveData<MutableList<RoomInviteModel>> = _roomInviteModel

    private val _userModel = MutableLiveData<UserModel>()
    val userModel: LiveData<UserModel> = _userModel

    private val _friendCountsMe = MutableLiveData<Int>()
    val friendCountsMe: LiveData<Int> = _friendCountsMe

    private val _friendCounts = MutableLiveData<Int>()
    val friendCounts: LiveData<Int> = _friendCounts

    private val _roomModel = MutableLiveData<RoomModel>()
    val roomModel: LiveData<RoomModel> = _roomModel

    fun loadRooms() {
        _isViewLoading.postValue(true)

        request(
            Singleton.apiClient().getRoomModels(),
            object : Callback<MutableList<RoomModel>> {
                override fun onError(msg: String) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: MutableList<RoomModel>) {
                    _isViewLoading.postValue(false)

                    if (obj.isEmpty()) {
                        _isEmptyList.postValue(true)
                    } else {
                        _roomModels.value = obj
                    }
                }
            })
    }

    fun loadFriendRequestModel() {
        _isViewLoading.postValue(true)

        request(
            Singleton.apiClient().getFriendRequestModels(),
            object : Callback<MutableList<FriendRequestModel>> {
                override fun onError(msg: String) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(msg)
                }

                @Suppress("UNCHECKED_CAST")
                override fun onSuccess(obj: MutableList<FriendRequestModel>) {
                    _isViewLoading.postValue(false)
                    _friendRequestModel.value = obj as MutableList<Any>

                    if (friendRequestModel.value.isNullOrEmpty()) {
                        _isEmptyList.postValue(true)
                    }
                }
            })
    }

    fun loadFriends() {
        _isViewLoading.postValue(true)

        request(
            Singleton.apiClient().getFriendModels(),
            object : Callback<MutableList<FriendModel>> {
                override fun onError(msg: String) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(msg)
                }

                @Suppress("UNCHECKED_CAST")
                override fun onSuccess(obj: MutableList<FriendModel>) {
                    _isViewLoading.postValue(false)
                    _friendRequestModel.value = obj as MutableList<Any>

                    if (friendRequestModel.value.isNullOrEmpty()) {
                        _isEmptyList.postValue(true)
                    }
                }
            })
    }

    fun loadRoomInviteModel() {
        _isViewLoading.postValue(true)

        request(
            Singleton.apiClient().getRoomInviteModels(),
            object : Callback<MutableList<RoomInviteModel>> {
                override fun onError(msg: String) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: MutableList<RoomInviteModel>) {
                    _isViewLoading.postValue(false)

                    if (obj.isNullOrEmpty()) {
                        _isEmptyList.postValue(true)
                    } else {
                        _roomInviteModel.value = obj
                    }
                }
            })
    }

    fun loadUserModel() {
        _isViewLoading.postValue(true)

        request(
            Singleton.apiClient().getUserModelMe(),
            object : Callback<UserModel> {
                override fun onError(msg: String) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: UserModel) {
                    _userModel.value = obj
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun loadFriendsCountMe() = request(
        Singleton.apiClient().getFriendsCounts(),
        object : Callback<Int> {
            override fun onError(msg: String) {
            }

            override fun onSuccess(obj: Int) {
                _friendCountsMe.value = obj
            }
        })

    fun loadFriendsCount(userId: String) = request(
        Singleton.apiClient().getFriendsCounts(userId),
        object : Callback<Int> {
            override fun onError(msg: String) {
            }

            override fun onSuccess(obj: Int) {
                _friendCounts.value = obj
            }
        })

    fun loadRoomModel(roomId: Long) = request(
        Singleton.apiClient().getRoomModel(roomId),
        object : Callback<RoomModel> {
            override fun onError(msg: String) {
            }

            override fun onSuccess(obj: RoomModel) {
                _roomModel.value = obj
            }
        })


}
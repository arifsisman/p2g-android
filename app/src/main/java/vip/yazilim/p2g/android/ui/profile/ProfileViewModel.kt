package vip.yazilim.p2g.android.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.P2GRequest
import vip.yazilim.p2g.android.model.p2g.FriendRequestModel
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.ViewModelBase

/**
 * @author mustafaarifsisman - 04.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class ProfileViewModel : ViewModelBase() {

    private val _userModel = MutableLiveData<UserModel>()
    val userModel: LiveData<UserModel> = _userModel

    private val _friendRequestModel = MutableLiveData<FriendRequestModel>()
    val friendRequestModel: LiveData<FriendRequestModel> = _friendRequestModel

    fun loadUserModel() {
        _isViewLoading.postValue(true)

        P2GRequest.build(
            ApiClient.build().getUserModelMe(),
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

    fun loadFriendsCount() {
        P2GRequest.build(
            ApiClient.build().getFriendRequestModel(),
            object : Callback<FriendRequestModel> {
                override fun onError(msg: String) {
                }

                override fun onSuccess(obj: FriendRequestModel) {
                    _friendRequestModel.value = obj
                }
            })
    }
}
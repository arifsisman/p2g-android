package vip.yazilim.p2g.android.ui.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.model.p2g.FriendRequestModel
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.ViewModelBase

class FriendsViewModel : ViewModelBase() {

    private val _friendRequestModels = MutableLiveData<MutableList<FriendRequestModel>>()
    val friendRequestModels: LiveData<MutableList<FriendRequestModel>> = _friendRequestModels

    private val _friends = MutableLiveData<MutableList<UserModel>>()
    val friends: LiveData<MutableList<UserModel>> = _friends

    fun loadFriendRequestModel() {
        _isViewLoading.postValue(true)
        //TODO: make request

    }

    fun loadFriends() {
        _isViewLoading.postValue(true)
        //TODO: make request

    }
}
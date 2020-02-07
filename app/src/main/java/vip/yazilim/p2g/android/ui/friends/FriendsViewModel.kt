package vip.yazilim.p2g.android.ui.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.P2GRequest
import vip.yazilim.p2g.android.model.p2g.FriendRequestModel
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.ViewModelBase

class FriendsViewModel : ViewModelBase() {

    private val _data = MutableLiveData<MutableList<Any>>()
    val data: LiveData<MutableList<Any>> = _data

    fun loadFriendRequestModel() {
        _isViewLoading.postValue(true)

        P2GRequest.build(
            ApiClient.build().getFriendRequestModel(),
            object : Callback<MutableList<FriendRequestModel>> {
                override fun onError(msg: String) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: MutableList<FriendRequestModel>) {
                    _isViewLoading.postValue(false)

                    if (obj.isEmpty()) {
                        _isEmptyList.postValue(true)
                    } else {
                        _data.value = obj as MutableList<Any>
                    }
                }
            })

    }

    fun loadFriends() {
        _isViewLoading.postValue(true)

        P2GRequest.build(
            ApiClient.build().getFriends(),
            object : Callback<MutableList<UserModel>> {
                override fun onError(msg: String) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: MutableList<UserModel>) {
                    _isViewLoading.postValue(false)

                    if (obj.isEmpty()) {
                        _isEmptyList.postValue(true)
                    } else {
                        _data.value = obj as MutableList<Any>
                    }
                }
            })
    }
}
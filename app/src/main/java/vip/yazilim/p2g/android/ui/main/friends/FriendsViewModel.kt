package vip.yazilim.p2g.android.ui.main.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.model.p2g.FriendModel
import vip.yazilim.p2g.android.model.p2g.FriendRequestModel
import vip.yazilim.p2g.android.ui.ViewModelBase
import vip.yazilim.p2g.android.util.refrofit.Singleton

class FriendsViewModel : ViewModelBase() {

    private val _data = MutableLiveData<MutableList<Any>>()
    val data: LiveData<MutableList<Any>> = _data

    fun loadFriendRequestModel() {
        _isViewLoading.postValue(true)

        request(
            Singleton.apiClient().getFriendRequestModel(),
            object : Callback<MutableList<FriendRequestModel>> {
                override fun onError(msg: String) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(msg)
                }

                @Suppress("UNCHECKED_CAST")
                override fun onSuccess(obj: MutableList<FriendRequestModel>) {
                    _isViewLoading.postValue(false)
                    _data.value = obj as MutableList<Any>
                }
            })
    }

    fun loadFriends() {
        _isViewLoading.postValue(true)

        request(
            Singleton.apiClient().getFriends(),
            object : Callback<MutableList<FriendModel>> {
                override fun onError(msg: String) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(msg)
                }

                @Suppress("UNCHECKED_CAST")
                override fun onSuccess(obj: MutableList<FriendModel>) {
                    _isViewLoading.postValue(false)
                    _data.value = obj as MutableList<Any>
                }
            })
    }
}
package vip.yazilim.p2g.android.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.ViewModelBase
import vip.yazilim.p2g.android.util.refrofit.Singleton

/**
 * @author mustafaarifsisman - 04.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class ProfileViewModel : ViewModelBase() {

    private val _userModel = MutableLiveData<UserModel>()
    val userModel: LiveData<UserModel> = _userModel

    private val _friendCounts = MutableLiveData<Int>()
    val friendCounts: LiveData<Int> = _friendCounts

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

    fun loadFriendsCount() = request(
            Singleton.apiClient().getFriendsCounts(),
            object : Callback<Int> {
                override fun onError(msg: String) {
                }

                override fun onSuccess(obj: Int) {
                    _friendCounts.value = obj
                }
            })
}
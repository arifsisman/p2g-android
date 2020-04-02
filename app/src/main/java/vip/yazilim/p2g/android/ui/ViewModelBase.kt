package vip.yazilim.p2g.android.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author mustafaarifsisman - 04.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
abstract class ViewModelBase : ViewModel() {

    val isViewLoading = MutableLiveData<Boolean>()
    val onMessageError = MutableLiveData<String>()
    val onMessageInfo = MutableLiveData<String>()

}
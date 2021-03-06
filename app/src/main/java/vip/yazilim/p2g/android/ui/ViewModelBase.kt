package vip.yazilim.p2g.android.ui

import androidx.lifecycle.ViewModel

/**
 * @author mustafaarifsisman - 04.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
abstract class ViewModelBase : ViewModel() {

    val onViewLoading = SingleLiveEvent<Boolean>()
    val onEmptyList = SingleLiveEvent<Boolean>()
    val onMessageError = SingleLiveEvent<String>()
    val onMessageInfo = SingleLiveEvent<String>()

}
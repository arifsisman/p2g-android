package vip.yazilim.p2g.android.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author mustafaarifsisman - 21.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class UserViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserViewModel() as T
    }
}
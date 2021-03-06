package vip.yazilim.p2g.android.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author mustafaarifsisman - 21.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class MainViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.newInstance()
    }

}
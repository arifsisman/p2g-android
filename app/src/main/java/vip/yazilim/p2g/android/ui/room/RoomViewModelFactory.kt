package vip.yazilim.p2g.android.ui.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vip.yazilim.p2g.android.ui.room.roomqueue.RoomViewModel

/**
 * @author mustafaarifsisman - 24.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RoomViewModel() as T
    }
}
package vip.yazilim.p2g.android.ui.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vip.yazilim.p2g.android.model.p2g.Song

/**
 * @author mustafaarifsisman - 24.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class PlayerViewModel : ViewModel() {
    private var _playerSongList = MutableLiveData<MutableList<Song>>()
    val playerSongList: LiveData<MutableList<Song>> = _playerSongList
}
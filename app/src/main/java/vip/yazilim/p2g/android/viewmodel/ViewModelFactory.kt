package vip.yazilim.p2g.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vip.yazilim.p2g.android.model.RoomDataSource
import vip.yazilim.p2g.android.ui.home.HomeViewModel

class ViewModelFactory(private val repository:RoomDataSource):ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }

}
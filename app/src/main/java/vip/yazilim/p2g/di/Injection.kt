package vip.yazilim.p2g.di

import vip.yazilim.p2g.android.model.RoomDataSource
import vip.yazilim.p2g.android.model.RoomRepository

object Injection {

    fun roomProviderRepository():RoomDataSource{
        return RoomRepository()
    }
}
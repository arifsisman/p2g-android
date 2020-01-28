package vip.yazilim.p2g.android.repository.p2g

import vip.yazilim.p2g.android.api.generic.Callback

interface RoomDataSource {
    fun getRoomModels(callback: Callback)
    fun cancel()
}
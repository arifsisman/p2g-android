package vip.yazilim.p2g.android.repository.p2g

import vip.yazilim.p2g.android.util.data.OperationCallback

interface RoomDataSource {
    fun getRoomModels(callback: OperationCallback)
    fun cancel()
}
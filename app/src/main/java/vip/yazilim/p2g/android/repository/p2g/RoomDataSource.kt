package vip.yazilim.p2g.android.repository.p2g

import vip.yazilim.p2g.android.api.helper.OperationCallback

interface RoomDataSource {
    fun getRoomModels(callback: OperationCallback)
    fun cancel()
}
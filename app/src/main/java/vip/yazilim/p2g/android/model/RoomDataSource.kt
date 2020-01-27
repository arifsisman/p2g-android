package vip.yazilim.p2g.android.model

import vip.yazilim.p2g.android.data.OperationCallback

interface RoomDataSource {

    fun getRoomModels(callback: OperationCallback)
    fun cancel()
}
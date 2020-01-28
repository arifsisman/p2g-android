package vip.yazilim.p2g.android.repository.p2g

import android.util.Log
import retrofit2.Call
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.helper.OperationCallback
import vip.yazilim.p2g.android.api.helper.RestResponse
import vip.yazilim.p2g.android.api.helper.Result
import vip.yazilim.p2g.android.api.helper.enqueue
import vip.yazilim.p2g.android.constant.GeneralConstants.LOG_TAG
import vip.yazilim.p2g.android.model.p2g.RoomModel

class RoomRepository : RoomDataSource {

    private var call: Call<RestResponse<List<RoomModel>>>? = null

    override fun getRoomModels(callback: OperationCallback) {
        call = ApiClient.build()?.getRoomModels()
        call?.enqueue { result ->
            when (result) {
                is Result.Success -> {
                    if (result.response.isSuccessful) {
                        val data = result.response.body()?.data
                        Log.v(LOG_TAG, "Response Data -> $data")
                        callback.onSuccess(data)
                    } else {
                        callback.onError(result.response.message())
                    }
                }
                is Result.Failure -> {
                    callback.onError(result.error.message)
                }
            }
        }
    }

    override fun cancel() {
        call?.cancel()
    }
}
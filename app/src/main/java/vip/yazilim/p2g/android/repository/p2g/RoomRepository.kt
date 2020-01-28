package vip.yazilim.p2g.android.repository.p2g

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vip.yazilim.p2g.android.constant.GeneralConstants.LOG_TAG
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.util.data.ApiClient
import vip.yazilim.p2g.android.util.data.OperationCallback
import vip.yazilim.p2g.android.util.data.RestResponse

class RoomRepository: RoomDataSource {

    private var call:Call<RestResponse<List<RoomModel>>>?=null

    override fun getRoomModels(callback: OperationCallback) {
        call= ApiClient.build()?.getRoomModels()
        call?.enqueue(object : Callback<RestResponse<List<RoomModel>>> {
            override fun onFailure(call: Call<RestResponse<List<RoomModel>>>, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<RestResponse<List<RoomModel>>>, response: Response<RestResponse<List<RoomModel>>>) {
                response.body()?.let {
                    if(response.isSuccessful){
                        Log.v(LOG_TAG, "data ${it.data}")
                        callback.onSuccess(it.data)
                    }else{
                        callback.onError(it.message)
                    }
                }
            }
        })
    }

    override fun cancel() {
        call?.cancel()
    }
}
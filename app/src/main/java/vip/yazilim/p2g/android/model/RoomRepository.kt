package vip.yazilim.p2g.android.model

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vip.yazilim.p2g.android.constant.GeneralConstants.LOG_TAG
import vip.yazilim.p2g.android.data.ApiClient
import vip.yazilim.p2g.android.data.OperationCallback
import vip.yazilim.p2g.android.data.RoomModelResponse

class RoomRepository:RoomDataSource {

    private var call:Call<RoomModelResponse>?=null

    override fun getRoomModels(callback: OperationCallback) {
        call= ApiClient.build()?.getRoomModels()
        call?.enqueue(object :Callback<RoomModelResponse>{
            override fun onFailure(call: Call<RoomModelResponse>, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<RoomModelResponse>, response: Response<RoomModelResponse>) {
                response.body()?.let {
                    if(response.isSuccessful){
                        Log.v(LOG_TAG, "data ${it.data}")
                        callback.onSuccess(it.data)
                    }else{
                        callback.onError(it.msg)
                    }
                }
            }
        })
    }

    override fun cancel() {
        call?.cancel()
    }
}
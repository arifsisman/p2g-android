package vip.yazilim.p2g.android.api.generic

import android.util.Log
import retrofit2.Call
import vip.yazilim.p2g.android.constant.GeneralConstants


/**
 * @author mustafaarifsisman - 28.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
abstract class Request {
    companion object {
        fun <T> build(call: Call<P2GResponse<T>>, callback: Callback<T>) {
            call.enqueue { result ->
                when (result) {
                    is Result.Success -> {
                        if (result.response.isSuccessful) {
                            val data = result.response.body()?.data
                            Log.v(GeneralConstants.LOG_TAG, "Response Data -> $data")
                            callback.onSuccess(data!!)
                        } else {
                            callback.onError(result.response.errorBody()!!.string())
                        }
                    }
                    is Result.Failure -> {
                        callback.onError(result.error.message as String)
                    }
                }
            }
        }
    }
}
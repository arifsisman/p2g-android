package vip.yazilim.p2g.android.api.generic

import android.util.Log
import retrofit2.Call
import vip.yazilim.p2g.android.constant.GeneralConstants.REQUEST_TAG


/**
 * @author mustafaarifsisman - 28.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
abstract class P2GRequest {
    companion object {
        inline fun <reified T> build(call: Call<P2GResponse<T>>?, callback: Callback<T>?) {
            call?.enqueue { result ->
                when (result) {
                    is Result.Success -> {
                        if (result.response.isSuccessful) {
                            callback?.onSuccess(result.response.body()?.data as T)
                        } else {
                            result.response.errorBody()?.let {
                                Log.d(REQUEST_TAG, it.string())
                                callback?.onError(it.string())
                            }
                        }
                    }
                    is Result.Failure -> {
                        val msg = result.error.message as String
                        Log.d(REQUEST_TAG, msg)
                        callback?.onError(msg)
                    }
                }
            }
        }
    }
}
package vip.yazilim.p2g.android.api.generic

import android.util.Log
import retrofit2.Call
import vip.yazilim.p2g.android.constant.GeneralConstants

/**
 * @author mustafaarifsisman - 29.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
abstract class SpotifyRequest {
    companion object {
        inline fun <reified T> build(call: Call<T>, callback: Callback<T>) {
            call.enqueue { result ->
                when (result) {
                    is Result.Success -> {
                        if (result.response.isSuccessful) {
                            val data = result.response.body()
                            Log.v(GeneralConstants.LOG_TAG, "Response Data -> $data")
                            callback.onSuccess(data as T)
                        } else {
                            callback.onError(result.response.message())
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
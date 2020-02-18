package vip.yazilim.p2g.android.api.generic

import android.util.Log
import retrofit2.Call
import vip.yazilim.p2g.android.constant.GeneralConstants.SPOTIFY_REQUEST_TAG

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
                            callback.onSuccess(result.response.body() as T)
                        } else {
                            val msg = result.response.message()
                            Log.d(SPOTIFY_REQUEST_TAG, msg)
                            callback.onError(msg)
                        }
                    }
                    is Result.Failure -> {
                        val msg = result.error.message as String
                        Log.d(SPOTIFY_REQUEST_TAG, msg)
                        callback.onError(msg)
                    }
                }
            }
        }
    }
}
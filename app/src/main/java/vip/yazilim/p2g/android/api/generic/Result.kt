package vip.yazilim.p2g.android.api.generic

import retrofit2.Call
import retrofit2.Response

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
sealed class Result<T> {
    data class Success<T>(val call: Call<T>, val response: Response<T>) : Result<T>()
    data class Failure<T>(val call: Call<T>, val error: Throwable) : Result<T>()
}

inline fun <reified T> Call<T>.resultHelper(crossinline result: (Result<T>) -> Unit) {
    enqueue(object : retrofit2.Callback<T> {
        override fun onFailure(call: Call<T>, error: Throwable) {
            result(Result.Failure(call, error))
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            result(Result.Success(call, response))
        }
    })
}
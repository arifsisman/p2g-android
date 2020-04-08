package vip.yazilim.p2g.android.api

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.Response
import vip.yazilim.p2g.android.api.generic.Result
import vip.yazilim.p2g.android.constant.ApiConstants
import vip.yazilim.p2g.android.constant.TokenConstants
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton
import vip.yazilim.p2g.android.util.event.UnauthorizedEvent
import vip.yazilim.p2g.android.util.gson.ThreeTenGsonAdapter


object Api {
    lateinit var client: Endpoints

    fun build(accessToken: String) {
        val gson = ThreeTenGsonAdapter.registerLocalDateTime(GsonBuilder()).create()
        val builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))

        val httpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor(accessToken))
            .addInterceptor(UnauthorizedInterceptor())
            .addInterceptor(loggingInterceptor()).build()

        val retrofit: Retrofit = builder.client(httpClient).build()

        client = retrofit.create(Endpoints::class.java) as Endpoints
        client.updateAccessToken(accessToken).queue(null)
        SharedPrefSingleton.write(TokenConstants.ACCESS_TOKEN, accessToken)
    }

    internal class UnauthorizedInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val response: okhttp3.Response = chain.proceed(chain.request())
            if (response.code == 401) EventBus.getDefault().post(UnauthorizedEvent.instance)
            return response
        }
    }

    internal class HeaderInterceptor(private val accessToken: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            return chain.run {
                proceed(
                    request()
                        .newBuilder()
                        .addHeader("Authorization", "Bearer $accessToken")
                        .build()
                )
            }
        }
    }

    private fun loggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    inline fun <reified T> Call<Response<T>>.queue(callback: Callback<T>?) =
        this.enqueueRequest { result ->
            when (result) {
                is Result.Success -> if (result.response.isSuccessful) {
                    callback?.onSuccess(result.response.body()?.data as T)
                } else {
                    val msg = result.response.errorBody()!!.string()
                    Log.d("Request not successful ", msg)
                    callback?.onError(msg)
                }
                is Result.Failure -> {
                    val msg = result.error.message as String
                    Log.d("Request failed ", msg)
                    callback?.onError(msg)
                }
            }
        }

    //todo merge with queue
    inline fun <reified T> Call<T>.enqueueRequest(crossinline result: (Result<T>) -> Unit) =
        enqueue(object : retrofit2.Callback<T> {
            override fun onFailure(call: Call<T>, error: Throwable) = result(
                Result.Failure(call, error)
            )

            override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) = result(
                Result.Success(call, response)
            )
        })

}
package vip.yazilim.p2g.android.api

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import vip.yazilim.p2g.android.constant.ApiConstants
import vip.yazilim.p2g.android.util.event.UnauthorizedEvent
import vip.yazilim.p2g.android.util.gson.ThreeTenGsonAdapter


object Api {
    lateinit var client: Endpoints
    private lateinit var httpClient: OkHttpClient

    fun build(accessToken: String) {
        val gson = ThreeTenGsonAdapter.registerLocalDateTime(GsonBuilder()).create()
        val builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))

        httpClient = OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor(accessToken))
            .addInterceptor(UnauthorizedInterceptor())
            .addInterceptor(loggingInterceptor())
            .build()

        val retrofit: Retrofit = builder.client(httpClient).build()

        client = retrofit.create(Endpoints::class.java)
        client.updateAccessToken(accessToken).queueAndForget()
    }

    internal class UnauthorizedInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val response: okhttp3.Response = chain.proceed(chain.request())
            if (response.code == 401 || response.code == 429) {
                EventBus.getDefault().post(UnauthorizedEvent.instance)
            }
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

    fun roomWebSocketClient(roomId: Long): StompClient? {
        return if (this::httpClient.isInitialized) {
            Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                "${ApiConstants.BASE_WS_URL_ROOM}/$roomId",
                null,
                httpClient
            )
        } else {
            null
        }
    }

    fun userWebSocketClient(userId: String): StompClient? {
        return if (this::httpClient.isInitialized) {
            Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                "${ApiConstants.BASE_WS_URL_USER}/$userId",
                null,
                httpClient
            )
        } else {
            null
        }
    }

    inline fun <T> Call<RestResponse<T>>.queue(
        crossinline onSuccess: (T) -> Unit,
        crossinline onFailure: ((String) -> Unit)
    ) =
        this.enqueue(object : retrofit2.Callback<RestResponse<T>> {
            override fun onFailure(call: Call<RestResponse<T>>, error: Throwable) {
                error.message?.let { onFailure(it) }
            }

            override fun onResponse(
                call: Call<RestResponse<T>>,
                response: retrofit2.Response<RestResponse<T>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.data?.let { onSuccess(it) }
                } else {
                    response.errorBody()?.string()?.let { onFailure(it) }
                }
            }
        })

    fun <T> Call<RestResponse<T>>.queueAndForget() =
        this.enqueue(object : retrofit2.Callback<RestResponse<T>> {
            override fun onFailure(call: Call<RestResponse<T>>, error: Throwable) {
            }

            override fun onResponse(
                call: Call<RestResponse<T>>,
                response: retrofit2.Response<RestResponse<T>>
            ) {
            }
        })

    inline fun <T> Call<RestResponse<T>>.queueAndCallbackOnSuccess(
        crossinline onSuccess: (T) -> Unit
    ) =
        this.enqueue(object : retrofit2.Callback<RestResponse<T>> {
            override fun onFailure(call: Call<RestResponse<T>>, error: Throwable) {
            }

            override fun onResponse(
                call: Call<RestResponse<T>>,
                response: retrofit2.Response<RestResponse<T>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.data?.let { onSuccess(it) }
                }
            }
        })

    inline fun <T> Call<RestResponse<T>>.queueAndCallbackOnFailure(
        crossinline onFailure: ((String) -> Unit)
    ) =
        this.enqueue(object : retrofit2.Callback<RestResponse<T>> {
            override fun onFailure(call: Call<RestResponse<T>>, error: Throwable) {
                error.message?.let { onFailure(it) }
            }

            override fun onResponse(
                call: Call<RestResponse<T>>,
                response: retrofit2.Response<RestResponse<T>>
            ) {
                if (!response.isSuccessful) {
                    response.errorBody()?.string()?.let { onFailure(it) }
                }
            }
        })
}

package vip.yazilim.p2g.android.data

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import vip.yazilim.p2g.android.constant.TokenConstants
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton
import vip.yazilim.p2g.android.util.gson.ThreeTenGsonAdapter
import vip.yazilim.p2g.android.util.refrofit.TokenAuthenticator

object ApiClient {

    private const val API_BASE_URL = "http://192.168.1.39:8080"

    private var servicesApiInterface: ServicesApiInterface? = null

    fun build(): ServicesApiInterface? {
        val accessToken =
            SharedPrefSingleton.read(TokenConstants.ACCESS_TOKEN, TokenConstants.UNDEFINED)

        val httpClient = OkHttpClient.Builder()
        httpClient
            .authenticator(TokenAuthenticator())
            .addInterceptor(interceptor())
            .addInterceptor {
                it.proceed(
                    it.request().newBuilder().addHeader(
                        "Authorization",
                        "Bearer $accessToken"
                    ).build()
                )
            }

        val gson = ThreeTenGsonAdapter.registerLocalDateTime(GsonBuilder()).create()
        val builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))

        val retrofit: Retrofit = builder
            .client(httpClient.build())
            .build()

        servicesApiInterface = retrofit.create(ServicesApiInterface::class.java)

        return servicesApiInterface as ServicesApiInterface
    }

    private fun interceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level=HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    interface ServicesApiInterface {
        @GET("/api/room/model/")
        fun getRoomModels(): Call<RestResponse<List<RoomModel>>>
    }
}
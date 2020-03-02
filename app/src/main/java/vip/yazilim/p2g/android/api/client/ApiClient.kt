package vip.yazilim.p2g.android.api.client

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vip.yazilim.p2g.android.api.Play2GetherWebApi
import vip.yazilim.p2g.android.constant.ApiConstants.BASE_URL
import vip.yazilim.p2g.android.constant.TokenConstants
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton
import vip.yazilim.p2g.android.util.gson.ThreeTenGsonAdapter
import vip.yazilim.p2g.android.util.refrofit.TokenAuthenticator

object ApiClient {

    fun build(): Play2GetherWebApi {

        val httpClient = OkHttpClient.Builder()
        httpClient
            .authenticator(TokenAuthenticator())
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(loggingInterceptor())

        val gson = ThreeTenGsonAdapter.registerLocalDateTime(GsonBuilder()).create()
        val builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
        val retrofit: Retrofit = builder.client(httpClient.build()).build()

        return retrofit.create(Play2GetherWebApi::class.java) as Play2GetherWebApi
    }

    class HeaderInterceptor : Interceptor {
        private val accessToken =
            SharedPrefSingleton.read(TokenConstants.ACCESS_TOKEN, TokenConstants.UNDEFINED)

        override fun intercept(chain: Interceptor.Chain): Response = chain.run {
            proceed(
                request().newBuilder().addHeader(
                    "Authorization",
                    "Bearer $accessToken"
                ).build()
            )
        }
    }

    private fun loggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

}
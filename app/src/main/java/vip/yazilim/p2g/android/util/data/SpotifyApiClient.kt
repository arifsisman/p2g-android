package vip.yazilim.p2g.android.util.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vip.yazilim.p2g.android.api.SpotifyWebApi
import vip.yazilim.p2g.android.constant.ApiConstants

/**
 * @author mustafaarifsisman - 28.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
object SpotifyApiClient {

    private var servicesApiInterface: SpotifyWebApi? = null

    fun build(): SpotifyWebApi? {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor())

        val builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(ApiConstants.SPOTIFY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

        val retrofit: Retrofit = builder
            .client(httpClient.build())
            .build()

        servicesApiInterface = retrofit.create(
            SpotifyWebApi::class.java)

        return servicesApiInterface as SpotifyWebApi
    }

    private fun interceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }
}
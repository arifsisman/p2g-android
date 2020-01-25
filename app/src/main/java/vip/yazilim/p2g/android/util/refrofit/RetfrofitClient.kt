package vip.yazilim.p2g.android.util.refrofit

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vip.yazilim.p2g.android.constant.ApiConstants
import vip.yazilim.p2g.android.util.helper.GsonHelper


/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */

class RetrofitClient {
    companion object {
        fun getClient(accessToken: String): Retrofit {
            val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .registerTypeAdapter(DateTime::class.java, GsonHelper.DateDeserializer)
                .create()

            val httpClient = OkHttpClient.Builder()
            httpClient
                .authenticator(TokenAuthenticator())
                .addInterceptor {
                    it.proceed(
                        it.request().newBuilder().addHeader(
                            "Authorization",
                            "Bearer $accessToken"
                        ).build()
                    )
                }

            return Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)).client(httpClient.build())
                .build()
        }

        fun getSpotifyClient(): Retrofit {
            val httpClient = OkHttpClient.Builder()
            return Retrofit.Builder()
                .baseUrl(ApiConstants.SPOTIFY_BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create()).client(httpClient.build())
                .build()
        }
    }

}
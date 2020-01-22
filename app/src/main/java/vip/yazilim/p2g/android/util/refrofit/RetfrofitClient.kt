package vip.yazilim.p2g.android.util.refrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vip.yazilim.p2g.android.constant.ApiConstants
import vip.yazilim.p2g.android.util.oauth.TokenAuthenticator


/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */

class RetrofitClient {
    companion object {
        fun getClient(accessToken: String): Retrofit {
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
                .addConverterFactory(GsonConverterFactory.create()).client(httpClient.build())
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
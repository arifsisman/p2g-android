package vip.yazilim.p2g.android.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import vip.yazilim.p2g.android.constant.ApiConstants
import vip.yazilim.p2g.android.constant.SpotifyConstants
import vip.yazilim.p2g.android.model.spotify.TokenModel

/**
 * @author mustafaarifsisman - 28.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
object SpotifyApiClient {

    private var servicesApiInterface: SpotifyServicesApiInterface? = null

    fun build(): SpotifyServicesApiInterface? {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor())

        val builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(ApiConstants.SPOTIFY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

        val retrofit: Retrofit = builder
            .client(httpClient.build())
            .build()

        servicesApiInterface = retrofit.create(SpotifyServicesApiInterface::class.java)

        return servicesApiInterface as SpotifyServicesApiInterface
    }

    private fun interceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    interface SpotifyServicesApiInterface {
        @FormUrlEncoded
        @POST("/api/token")
        fun getTokens(
            @Field("client_id") clientId: String = SpotifyConstants.CLIENT_ID,
            @Field("client_secret") clientSecret: String = SpotifyConstants.CLIENT_SECRET,
            @Field("grant_type") grantType: String = SpotifyConstants.GRANT_TYPE_AUTHORIZATION_CODE_REQUEST,
            @Field("code") code: String,
            @Field("redirect_uri") redirectUri: String = SpotifyConstants.REDIRECT_URI
        ): Call<TokenModel>

        @FormUrlEncoded
        @POST("/api/token")
        fun refreshExpiredToken(
            @Field("client_id") clientId: String = SpotifyConstants.CLIENT_ID,
            @Field("client_secret") clientSecret: String = SpotifyConstants.CLIENT_SECRET,
            @Field("grant_type") grantType: String = SpotifyConstants.GRANT_TYPE_REFRESH_TOKEN_REQUEST,
            @Field("refresh_token") refreshToken: String
        ): Call<TokenModel>
    }
}
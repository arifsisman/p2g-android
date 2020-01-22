package vip.yazilim.p2g.android.api.spotify

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import vip.yazilim.p2g.android.model.spotify.TokenResponse

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
interface SpotifyApi {

    @FormUrlEncoded
    @POST("api/token")
    fun getTokens(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String
    ): Call<TokenResponse>

}
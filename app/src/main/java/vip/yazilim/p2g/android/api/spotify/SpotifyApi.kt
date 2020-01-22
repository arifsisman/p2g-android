package vip.yazilim.p2g.android.api.spotify

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import vip.yazilim.p2g.android.constant.SpotifyConstants
import vip.yazilim.p2g.android.model.spotify.SpotifyTokenResponseModel

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
interface SpotifyApi {

        @FormUrlEncoded
//    @Headers("Content-Type: application/x-www-form-urlencoded")
//    @POST("api/token?grand_type=authorization_code")
    @POST("api/token")
//    fun getTokens(@Body codeRequestModel: SpotifyCodeRequestModel): Call<SpotifyTokenResponseModel>
    fun getTokens(@Field("client_id") clientId: String = SpotifyConstants.CLIENT_ID,
                  @Field("client_secret") clientSecret: String = SpotifyConstants.CLIENT_SECRET,
                  @Field("grant_type") grantType: String = "authorization_code",
                  @Field("code") code:String,
                  @Field("redirect_uri") redirectUri:String = SpotifyConstants.REDIRECT_URI): Call<SpotifyTokenResponseModel>

}
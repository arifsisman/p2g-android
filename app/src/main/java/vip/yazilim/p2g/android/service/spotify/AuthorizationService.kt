package vip.yazilim.p2g.android.service.spotify

import android.content.Context
import retrofit2.Call
import retrofit2.Response
import vip.yazilim.p2g.android.api.spotify.AuthorizationApi
import vip.yazilim.p2g.android.constant.SpotifyConstants
import vip.yazilim.p2g.android.model.spotify.TokenModel
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton
import vip.yazilim.p2g.android.util.helper.UIHelper
import vip.yazilim.p2g.android.util.refrofit.RetrofitClient

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
object AuthorizationService {
//    fun getTokensFromSpotify(context: Context, code: String):String {
//        RetrofitClient.getSpotifyClient().create(AuthorizationApi::class.java)
//            .getTokens(
//                SpotifyConstants.CLIENT_ID,
//                SpotifyConstants.CLIENT_SECRET,
//                "authorization_code",
//                code,
//                SpotifyConstants.REDIRECT_URI
//            )
//            .enqueue(object : retrofit2.Callback<TokenModel> {
//
//                override fun onResponse(
//                    call: Call<TokenModel>,
//                    model: Response<TokenModel>
//                ) {
//                    val tokenModel: TokenModel = model.body()!!
//                    SharedPrefSingleton.write("access_token", tokenModel.access_token)
//                    SharedPrefSingleton.write("refresh_token", tokenModel.refresh_token)
//
//
//                }
//
//                override fun onFailure(
//                    call: Call<TokenModel>?,
//                    t: Throwable?
//                ) {
//                    UIHelper.showToastLong(context, "Failed to login Spotify")
//                }
//            }
//
//            )
//
//        return SharedPrefSingleton.read("access_token", null).toString()
//    }

        fun getTokensFromSpotify(context: Context, code: String):String {
        RetrofitClient.getSpotifyClient().create(AuthorizationApi::class.java)
            .getTokens(
                SpotifyConstants.CLIENT_ID,
                SpotifyConstants.CLIENT_SECRET,
                "authorization_code",
                code,
                SpotifyConstants.REDIRECT_URI
            )
            .enqueue(object : retrofit2.Callback<TokenModel> {

                override fun onResponse(
                    call: Call<TokenModel>,
                    model: Response<TokenModel>
                ) {
                    val tokenModel: TokenModel = model.body()!!
                    SharedPrefSingleton.write("access_token", tokenModel.access_token)
                    SharedPrefSingleton.write("refresh_token", tokenModel.refresh_token)


                }

                override fun onFailure(
                    call: Call<TokenModel>?,
                    t: Throwable?
                ) {
                    UIHelper.showToastLong(context, "Failed to login Spotify")
                }
            }

            )

        return SharedPrefSingleton.read("access_token", null).toString()
    }


    fun refreshExpiredToken(refreshToken: String): String {
        RetrofitClient.getSpotifyClient().create(AuthorizationApi::class.java)
            .refreshExpiredToken(
                SpotifyConstants.CLIENT_ID,
                SpotifyConstants.CLIENT_SECRET,
                "refresh_token",
                refreshToken
            )
            .enqueue(object : retrofit2.Callback<TokenModel> {

                override fun onResponse(
                    call: Call<TokenModel>,
                    model: Response<TokenModel>
                ) {
                    val tokenModel: TokenModel = model.body()!!
                    SharedPrefSingleton.write("access_token", tokenModel.access_token)
                    SharedPrefSingleton.write("refresh_token", tokenModel.refresh_token)
                }

                override fun onFailure(call: Call<TokenModel>, t: Throwable) {
                    //TODO: log failure
                }

            }

            )

        return SharedPrefSingleton.read("access_token", null).toString()
    }
}

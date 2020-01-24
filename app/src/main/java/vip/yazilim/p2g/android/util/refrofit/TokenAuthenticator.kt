package vip.yazilim.p2g.android.util.refrofit

import android.util.Log
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import vip.yazilim.p2g.android.api.p2g.spotify.LoginApi
import vip.yazilim.p2g.android.api.spotify.AuthorizationApi
import vip.yazilim.p2g.android.constant.SpotifyConstants
import vip.yazilim.p2g.android.data.spotify.TokenModel
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class TokenAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = SharedPrefSingleton.read("refresh_token", null)
        val updatedToken =  refreshExpiredToken(refreshToken.toString())

        Log.d("Play2Gether", "Token refreshed")
        SharedPrefSingleton.write("access_token", updatedToken)

        updateAccessTokenOnPlay2Gether(updatedToken)

        return response.request.newBuilder()
            .header("Authorization", "Bearer $updatedToken")
            .build()
    }

    private fun refreshExpiredToken(refreshToken: String): String {
        RetrofitClient.getSpotifyClient().create(AuthorizationApi::class.java)
            .refreshExpiredToken(
                SpotifyConstants.CLIENT_ID,
                SpotifyConstants.CLIENT_SECRET,
                SpotifyConstants.GRANT_TYPE_REFRESH_TOKEN_REQUEST,
                refreshToken
            ).enqueue { result ->
                when (result) {
                    is Result.Success -> {
                        val tokenModel: TokenModel = result.response.body()!!
                        SharedPrefSingleton.write("access_token", tokenModel.access_token)
                        SharedPrefSingleton.write("refresh_token", tokenModel.refresh_token)
                    }
                    is Result.Failure -> {
                        Log.d("Play2Gether", result.error.toString())
                    }
                }
            }
        return SharedPrefSingleton.read("access_token", null).toString()
    }

    private fun updateAccessTokenOnPlay2Gether(accessToken: String) {
        RetrofitClient.getClient(accessToken).create(LoginApi::class.java)
            .updateAccessToken(accessToken).enqueue { result ->
                when (result) {
                    is Result.Failure -> {
                        Log.d("Play2Gether", result.error.toString())
                    }
                }
            }
    }
}

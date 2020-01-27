package vip.yazilim.p2g.android.util.refrofit

import android.util.Log
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import vip.yazilim.p2g.android.api.SpotifyWebApi
import vip.yazilim.p2g.android.api.spotify.AuthorizationApi
import vip.yazilim.p2g.android.constant.GeneralConstants.LOG_TAG
import vip.yazilim.p2g.android.constant.SpotifyConstants
import vip.yazilim.p2g.android.constant.TokenConstants
import vip.yazilim.p2g.android.model.spotify.TokenModel
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class TokenAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken =
            SharedPrefSingleton.read(TokenConstants.REFRESH_TOKEN, TokenConstants.UNDEFINED)
        val updatedToken = refreshExpiredToken(refreshToken.toString())

        SharedPrefSingleton.write(TokenConstants.ACCESS_TOKEN, updatedToken)

        updateAccessTokenOnPlay2Gether(updatedToken)

        return response.request.newBuilder()
            .header("Authorization", "Bearer $updatedToken")
            .build()
    }


    companion object {
        fun refreshExpiredToken(refreshToken: String): String {
            RetrofitClient.getSpotifyClient().create(SpotifyWebApi::class.java)
                .refreshExpiredToken(
                    SpotifyConstants.CLIENT_ID,
                    SpotifyConstants.CLIENT_SECRET,
                    SpotifyConstants.GRANT_TYPE_REFRESH_TOKEN_REQUEST,
                    refreshToken
                ).enqueue { result ->
                    when (result) {
                        is Result.Success -> {
                            val tokenModel: TokenModel? = result.response.body()
                            if (tokenModel != null) {
                                SharedPrefSingleton.write(
                                    TokenConstants.ACCESS_TOKEN,
                                    tokenModel.access_token
                                )
                                SharedPrefSingleton.write(
                                    TokenConstants.REFRESH_TOKEN,
                                    tokenModel.refresh_token
                                )
                                Log.d(LOG_TAG, "Token refreshed")
                            }
                        }
                        is Result.Failure -> {
                            Log.d(LOG_TAG, result.error.toString())
                        }
                    }
                }
            return SharedPrefSingleton.read(TokenConstants.ACCESS_TOKEN, TokenConstants.UNDEFINED)
                .toString()
        }
    }

    private fun updateAccessTokenOnPlay2Gether(accessToken: String) {
        RetrofitClient.getClient().create(AuthorizationApi::class.java)
            .updateAccessToken(accessToken).enqueue { result ->
                when (result) {
                    is Result.Failure -> {
                        Log.d(LOG_TAG, result.error.toString())
                    }
                }
            }
    }
}

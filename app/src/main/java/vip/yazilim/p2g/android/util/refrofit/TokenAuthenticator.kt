package vip.yazilim.p2g.android.util.refrofit

import android.util.Log
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.client.SpotifyApiClient
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.api.generic.spotifyRequest
import vip.yazilim.p2g.android.constant.SpotifyConstants
import vip.yazilim.p2g.android.constant.TokenConstants
import vip.yazilim.p2g.android.model.spotify.TokenModel
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton
import vip.yazilim.p2g.android.util.helper.TAG

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class TokenAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken =
            SharedPrefSingleton.read(TokenConstants.REFRESH_TOKEN, TokenConstants.UNDEFINED)
        if (refreshToken == TokenConstants.UNDEFINED) return null

        spotifyRequest(
            SpotifyApiClient.build().refreshExpiredToken(
                SpotifyConstants.CLIENT_ID,
                SpotifyConstants.CLIENT_SECRET,
                SpotifyConstants.GRANT_TYPE_REFRESH_TOKEN_REQUEST,
                refreshToken!!
            ), object : Callback<TokenModel> {
                override fun onError(msg: String) {
                }

                override fun onSuccess(obj: TokenModel) {
                    SharedPrefSingleton.write(TokenConstants.ACCESS_TOKEN, obj.access_token)
                    SharedPrefSingleton.write(TokenConstants.REFRESH_TOKEN, obj.access_token)
                    obj.access_token?.let { updateAccessTokenOnPlay2Gether(it) }
                    Log.d(TAG, "Token refreshed. New access token is: $obj.access_token")
                    Singleton.buildApi()
                }
            })

        val updatedToken: String? =
            SharedPrefSingleton.read(TokenConstants.ACCESS_TOKEN, TokenConstants.UNDEFINED)

        return if (updatedToken == TokenConstants.UNDEFINED) null else
            response.request.newBuilder()
                .header(
                    "Authorization",
                    "Bearer ${updatedToken.toString()}"
                )
                .build()
    }


    companion object {
        fun updateAccessTokenOnPlay2Gether(accessToken: String) = request(
            ApiClient.build().updateAccessToken(accessToken), null
        )
    }
}


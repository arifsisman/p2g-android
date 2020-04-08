package vip.yazilim.p2g.android.api

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import vip.yazilim.p2g.android.constant.TokenConstants
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class TokenAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken =
            SharedPrefSingleton.read(TokenConstants.REFRESH_TOKEN, TokenConstants.UNDEFINED)
        if (refreshToken == TokenConstants.UNDEFINED) return null

        refreshToken()

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
        fun refreshToken() {
            //todo: get new access token with Spotify SDK and build new ApiClient
        }
    }
}


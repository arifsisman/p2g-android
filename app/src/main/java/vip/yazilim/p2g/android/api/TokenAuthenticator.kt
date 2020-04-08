package vip.yazilim.p2g.android.api

import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import vip.yazilim.p2g.android.Play2GetherApplication
import vip.yazilim.p2g.android.constant.SpotifyConstants
import vip.yazilim.p2g.android.constant.TokenConstants
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class TokenAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
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
        fun getAccessTokenFromSpotify() {
            val request: AuthenticationRequest = AuthenticationRequest
                .Builder(
                    SpotifyConstants.CLIENT_ID,
                    AuthenticationResponse.Type.TOKEN,
                    SpotifyConstants.REDIRECT_URI
                )
                .setShowDialog(false)
                .setScopes(SpotifyConstants.SCOPE)
                .build()

            AuthenticationClient.openLoginActivity(
                Play2GetherApplication.currentActivity,
                SpotifyConstants.AUTH_TOKEN_REQUEST_CODE,
                request
            )

            //todo handleUnauthorizedEvent
        }

        fun refreshToken() {
            //todo: get new access token with Spotify SDK and build new ApiClient
        }
    }
}


package vip.yazilim.p2g.android.util.helper

import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import vip.yazilim.p2g.android.Play2GetherApplication
import vip.yazilim.p2g.android.constant.SpotifyConstants

/**
 * @author mustafaarifsisman - 08.04.2020
 * @contact mustafaarifsisman@gmail.com
 */
class SpotifyHelper {
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
        }
    }
}
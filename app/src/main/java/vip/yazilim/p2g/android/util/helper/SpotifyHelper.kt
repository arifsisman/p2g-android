package vip.yazilim.p2g.android.util.helper

import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import vip.yazilim.p2g.android.Play2GetherApplication
import vip.yazilim.p2g.android.constant.SpotifyConstants

/**
 * @author mustafaarifsisman - 08.04.2020
 * @contact mustafaarifsisman@gmail.com
 */
class SpotifyHelper {
    companion object {
        fun getAccessTokenFromSpotify() {
            val request: AuthorizationRequest = AuthorizationRequest
                .Builder(
                    SpotifyConstants.CLIENT_ID,
                    AuthorizationResponse.Type.TOKEN,
                    SpotifyConstants.REDIRECT_URI
                )
                .setShowDialog(false)
                .setScopes(SpotifyConstants.SCOPE)
                .build()

            AuthorizationClient.openLoginActivity(
                Play2GetherApplication.currentActivity,
                SpotifyConstants.AUTH_TOKEN_REQUEST_CODE,
                request
            )
        }
    }
}
package vip.yazilim.p2g.android.util.oauth

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import vip.yazilim.p2g.android.service.spotify.AuthorizationService
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class TokenAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = SharedPrefSingleton.read("refresh_token", null)
        val updatedToken = refreshToken?.let { AuthorizationService.refreshExpiredToken(it) }

        println("Token refreshed")
        SharedPrefSingleton.write("access_token", updatedToken)

        return response.request.newBuilder()
            .header("Authorization", "Bearer $updatedToken")
            .build()
    }
}

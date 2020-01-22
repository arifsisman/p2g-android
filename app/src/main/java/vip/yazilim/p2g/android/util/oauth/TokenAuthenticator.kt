package vip.yazilim.p2g.android.util.oauth

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class TokenAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            println("expired!!! get new access token with refresh token")
            //TODO: get new access token
            // TODO: send new access token to p2g
            SharedPrefSingleton.remove("access_token")
        }
        return null
    }
}
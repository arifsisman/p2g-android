package vip.yazilim.p2g.android.data.p2g

import java.io.Serializable

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class OAuthToken(
    var userId: String,
    var accessToken: String,
    var refreshToken: String
) : Serializable
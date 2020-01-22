package vip.yazilim.p2g.android.model.spotify

import vip.yazilim.p2g.android.constant.SpotifyConstants

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class SpotifyCodeRequestModel(
    val client_id: String,
    val client_secret: String,
    val grant_type: String,
    val code: String,
    val redirect_uri: String
) {
    constructor(code: String) : this(
        SpotifyConstants.CLIENT_ID,
        SpotifyConstants.CLIENT_SECRET,
        "authorization_code",
        code,
        SpotifyConstants.REDIRECT_URI
    )
}
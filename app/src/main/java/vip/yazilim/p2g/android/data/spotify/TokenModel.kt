package vip.yazilim.p2g.android.data.spotify

import java.io.Serializable

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class TokenModel(
    val access_token: String,
    val token_type: String?,
    val expires_in: Int?,
    val refresh_token: String,
    val scope: String?
) : Serializable {
    constructor(access_token: String, refresh_token: String) : this(
        access_token,
        "",
        -1,
        refresh_token,
        ""
    )
}
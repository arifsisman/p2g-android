package vip.yazilim.p2g.android.data.spotify

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class TokenModel(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val refresh_token:String,
    val scope: String
)
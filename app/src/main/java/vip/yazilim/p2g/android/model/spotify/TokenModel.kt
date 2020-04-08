package vip.yazilim.p2g.android.model.spotify

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Parcelize
data class TokenModel(
    val access_token: String?,
    val token_type: String?,
    val expires_in: Int,
    val refresh_token: String?,
    val scope: String?
) : Parcelable
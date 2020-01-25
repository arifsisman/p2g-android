package vip.yazilim.p2g.android.data.p2g

import com.squareup.moshi.JsonClass
import org.threeten.bp.LocalDateTime
import java.io.Serializable

/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
@JsonClass(generateAdapter = true)
data class User(
    var id: String,
    var name: String,
    var email: String,
    var role: String?,
    var onlineStatus: String,
    var countryCode: String,
    var imageUrl: String,
    var anthem: String?,
    var spotifyProductType: String,
    var showActivityFlag: Boolean,
    var showFriendsFlag: Boolean,
    var creationDate: LocalDateTime
) : Serializable
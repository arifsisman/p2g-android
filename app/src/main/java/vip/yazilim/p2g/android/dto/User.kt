package vip.yazilim.p2g.android.dto

import org.threeten.bp.LocalDateTime

/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val onlineStatus: String,
    val countryCode: String,
    val imageUrl: String,
    val anthem: String,
    val spotifyProductType: String,
    val showActivityFlag: String,
    val showFriendsFlag: String,
    val creationDate: LocalDateTime
)
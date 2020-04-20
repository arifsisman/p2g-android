package vip.yazilim.p2g.android.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDateTime

/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Parcelize
data class User(
    var id: String,
    var name: String,
    var email: String,
    var systemRole: String,
    var onlineStatus: String,
    var countryCode: String?,
    var imageUrl: String?,
    var creationDate: LocalDateTime,
    var lastLogin: LocalDateTime?
) : Parcelable
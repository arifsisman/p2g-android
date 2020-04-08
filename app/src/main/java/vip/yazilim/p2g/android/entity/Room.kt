package vip.yazilim.p2g.android.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDateTime

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Parcelize
class Room(
    var id: Long,
    var name: String,
    var ownerId: String,
    var creationDate: LocalDateTime,
    var privateFlag: Boolean,
    var password: String?,
    var maxUsers: Int,
    var activeFlag: Boolean
) : Parcelable
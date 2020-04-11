package vip.yazilim.p2g.android.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDateTime

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Parcelize
data class FriendRequest(
    var id: Long,
    var senderId: String,
    var receiverId: String,
    var requestStatus: String,
    var requestDate: LocalDateTime
) : Parcelable
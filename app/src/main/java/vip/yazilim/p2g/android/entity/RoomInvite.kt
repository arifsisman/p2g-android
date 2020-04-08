package vip.yazilim.p2g.android.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDateTime

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Parcelize
data class RoomInvite(
    var id: Long,
    var roomId: Long,
    var inviterId: String,
    var receiverId: String,
    var invitationDate: LocalDateTime
) : Parcelable
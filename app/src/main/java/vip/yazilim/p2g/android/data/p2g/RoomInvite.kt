package vip.yazilim.p2g.android.data.p2g

import org.threeten.bp.LocalDateTime
import java.io.Serializable

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class RoomInvite(
    var id: Long,
    var roomId: Long,
    var userId: String,
    var invitationDate: LocalDateTime,
    var acceptedFlag: Boolean
) : Serializable
package vip.yazilim.p2g.android.model.p2g

import org.threeten.bp.LocalDateTime
import java.io.Serializable

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class RoomUser(
    var id: Long,
    var roomId: Long,
    var userId: String,
    var role: String,
    var joinDate: LocalDateTime,
    var activeFlag: Boolean
) : Serializable
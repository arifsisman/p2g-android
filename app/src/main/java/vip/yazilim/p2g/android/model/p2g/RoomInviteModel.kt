package vip.yazilim.p2g.android.model.p2g

import java.io.Serializable

/**
 * @author mustafaarifsisman - 02.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class RoomInviteModel(
    var roomInvites: List<RoomInvite>? = emptyList(),
    var roomModels: List<RoomModel>? = emptyList()
) : Serializable
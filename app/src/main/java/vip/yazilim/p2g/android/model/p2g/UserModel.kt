package vip.yazilim.p2g.android.model.p2g

import java.io.Serializable

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class UserModel(
    var user: User? = null,
    var room: Room? = null,
    var userDevices: List<UserDevice> = emptyList(),
    var roomUser: RoomUser? = null,
    var friends: List<User>? = emptyList(),
    var friendRequests: List<User>? = emptyList()
) : Serializable

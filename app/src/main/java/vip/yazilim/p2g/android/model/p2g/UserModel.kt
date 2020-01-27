package vip.yazilim.p2g.android.model.p2g

import java.io.Serializable

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class UserModel(
    var user: User,
    var room: Room?,
    var userDevices: List<UserDevice>,
    var roomUser: RoomUser?,
    var friends: List<User>?,
    var friendRequests: List<User>?
) : Serializable

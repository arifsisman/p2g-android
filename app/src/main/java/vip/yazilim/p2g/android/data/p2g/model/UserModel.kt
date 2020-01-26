package vip.yazilim.p2g.android.data.p2g.model

import vip.yazilim.p2g.android.data.p2g.Room
import vip.yazilim.p2g.android.data.p2g.RoomUser
import vip.yazilim.p2g.android.data.p2g.User
import java.io.Serializable

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class UserModel(
    var user: User,
    var room: Room?,
    var roomUser: RoomUser?,
    var friends: List<User>?,
    var friendRequests: List<User>?
) : Serializable

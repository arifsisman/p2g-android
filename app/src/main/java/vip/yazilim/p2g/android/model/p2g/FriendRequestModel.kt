package vip.yazilim.p2g.android.model.p2g

import java.io.Serializable

/**
 * @author mustafaarifsisman - 02.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class FriendRequestModel(
    var friendRequests: List<FriendRequest>? = emptyList(),
    var friendRequestUsers: List<User>? = emptyList(),
    var friends: List<UserModel>? = emptyList()
) : Serializable
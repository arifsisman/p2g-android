package vip.yazilim.p2g.android.model.p2g

import java.io.Serializable


/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class RoomModel(
    var room: Room,
    var userList: List<User>,
    var songList: List<Song>?,
    var invitedUserList: List<User>?
) : Serializable

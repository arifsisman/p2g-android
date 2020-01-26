package vip.yazilim.p2g.android.data.p2g.model

import vip.yazilim.p2g.android.data.p2g.Room
import vip.yazilim.p2g.android.data.p2g.Song
import vip.yazilim.p2g.android.data.p2g.User
import java.io.Serializable

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class SearchModel(
    var room: Room,
    var userList: List<User>,
    var songList: List<Song>?,
    var invitedUserList: List<User>?
) : Serializable

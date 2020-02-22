package vip.yazilim.p2g.android.constant.enums

/**
 * @author mustafaarifsisman - 23.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
enum class Role(val role: String) {
    UNDEFINED("undefined"),
    P2G_USER("p2g_user"),
    ROOM_USER("room_user"),
    ROOM_MODERATOR("room_moderator"),
    ROOM_ADMIN("room_admin"),
    ROOM_OWNER("room_owner");
}

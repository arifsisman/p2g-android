package vip.yazilim.p2g.android.constant.enums

/**
 * @author mustafaarifsisman - 23.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
enum class Role(val role: String) {
    UNDEFINED("Undefined"),
    P2G_USER("P2G_User"),
    ROOM_USER("User"),
    ROOM_MODERATOR("Moderator"),
    ROOM_ADMIN("Admin"),
    ROOM_OWNER("Owner");
}

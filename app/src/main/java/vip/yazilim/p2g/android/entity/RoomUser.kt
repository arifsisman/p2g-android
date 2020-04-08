package vip.yazilim.p2g.android.entity

import android.os.Parcelable
import com.stfalcon.chatkit.commons.models.IUser
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDateTime

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Parcelize
data class RoomUser(
    var id: Long,
    var roomId: Long,
    var userId: String,
    var userName: String,
    var role: String,
    var joinDate: LocalDateTime,
    var activeFlag: Boolean
) : Parcelable, IUser {
    override fun getId(): String? {
        return this.userId
    }

    override fun getName(): String? {
        return this.userName
    }

    override fun getAvatar(): String? {
        return null
    }
}
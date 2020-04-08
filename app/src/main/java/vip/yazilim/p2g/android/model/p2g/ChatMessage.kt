package vip.yazilim.p2g.android.model.p2g

import android.os.Parcelable
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.LocalDateTime
import vip.yazilim.p2g.android.entity.RoomUser
import vip.yazilim.p2g.android.util.helper.TimeHelper.Companion.toZonedDateTime
import java.util.*

/**
 * @author mustafaarifsisman - 24.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Parcelize
data class ChatMessage(
    var roomUser: RoomUser?,
    var message: String?,
    var timestamp: LocalDateTime?
) : IMessage, Parcelable {
    override fun getId(): String? {
        return this.roomUser?.userId + timestamp
    }

    override fun getUser(): IUser? {
        return this.roomUser
    }

    override fun getText(): String? {
        return this.message
    }

    override fun getCreatedAt(): Date {
        return DateTimeUtils.toDate(timestamp?.toZonedDateTime()?.toInstant())
    }
}
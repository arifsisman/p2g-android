package vip.yazilim.p2g.android.data.websocket

import com.squareup.moshi.JsonClass
import org.joda.time.DateTime
import java.io.Serializable

/**
 * @author mustafaarifsisman - 24.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
@JsonClass(generateAdapter = true)
data class ChatMessage(
    var userId: String,
    var userName: String,
    var roomId: String,
    var message: String,
    var timestamp: DateTime
) : Serializable



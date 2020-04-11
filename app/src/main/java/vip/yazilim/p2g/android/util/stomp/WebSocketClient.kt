package vip.yazilim.p2g.android.util.stomp

import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import vip.yazilim.p2g.android.Play2GetherApplication
import vip.yazilim.p2g.android.constant.ApiConstants

/**
 * @author mustafaarifsisman - 25.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class WebSocketClient {
    companion object {
        fun getRoomWebSocketClient(roomId: Long): StompClient {
            val accessToken = Play2GetherApplication.accessToken
            val header: MutableMap<String, String> = mutableMapOf()
            header["Authorization"] = "Bearer $accessToken"

            return Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                ApiConstants.BASE_WS_URL_ROOM + roomId,
                header
            )
        }

        fun getUserWebSocketClient(userId: String): StompClient {
            val accessToken = Play2GetherApplication.accessToken
            val header: MutableMap<String, String> = mutableMapOf()
            header["Authorization"] = "Bearer $accessToken"

            return Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                ApiConstants.BASE_WS_URL_USER + userId,
                header
            )
        }
    }
}
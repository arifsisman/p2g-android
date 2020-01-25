package vip.yazilim.p2g.android.util.stomp

import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import vip.yazilim.p2g.android.constant.ApiConstants
import vip.yazilim.p2g.android.constant.TokenConstants
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton

/**
 * @author mustafaarifsisman - 25.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class WebSocketClient {
    companion object {
        fun getRoomWebSocketClient(roomId: String): StompClient {
            val accessToken =
                SharedPrefSingleton.read(TokenConstants.ACCESS_TOKEN, TokenConstants.UNDEFINED)
            val header: MutableMap<String, String> = mutableMapOf()
            header["Authorization"] = "Bearer $accessToken"

            return Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                ApiConstants.BASE_WS_URL_ROOM + roomId,
                header
            ).withClientHeartbeat(0).withServerHeartbeat(0)
        }

        fun getUserWebSocketClient(userId: String): StompClient {
            val accessToken =
                SharedPrefSingleton.read(TokenConstants.ACCESS_TOKEN, TokenConstants.UNDEFINED)
            val header: MutableMap<String, String> = mutableMapOf()
            header["Authorization"] = "Bearer $accessToken"

            return Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                ApiConstants.BASE_WS_URL_USER + userId,
                header
            ).withClientHeartbeat(0).withServerHeartbeat(0)
        }
    }
}
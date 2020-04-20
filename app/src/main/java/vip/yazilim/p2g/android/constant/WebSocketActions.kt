package vip.yazilim.p2g.android.constant

/**
 * @author mustafaarifsisman - 27.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
object WebSocketActions {
    const val ACTION_SONG_LIST_RECEIVE = "SongListReceive"
    const val ACTION_USER_LIST_RECEIVE = "UserListReceive"
    const val ACTION_ROOM_STATUS_RECEIVE = "RoomStatusReceive"
    const val ACTION_ROOM_INVITE_RECEIVE = "RoomInviteReceive"
    const val ACTION_MESSAGE_RECEIVE = "MessageReceive"
    const val ACTION_MESSAGE_SEND = "MessageSend"

    const val CHECK_WEBSOCKET_CONNECTION = "CheckWebSocketConnection"
    const val ACTION_ROOM_SOCKET_RECONNECTING = "RoomSocketReconnect"
    const val ACTION_ROOM_SOCKET_CONNECTED = "RoomSocketConnect"
    const val ACTION_ROOM_SOCKET_CLOSED = "RoomSocketClose"
}
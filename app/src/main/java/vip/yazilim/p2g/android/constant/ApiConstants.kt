package vip.yazilim.p2g.android.constant

import vip.yazilim.p2g.android.BuildConfig

/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
object ApiConstants {

    private const val HOST = BuildConfig.URL
    private const val PORT = "8097"
    const val BASE_URL = "http://$HOST:$PORT"
    const val BASE_WS_URL_ROOM = "ws://$HOST:$PORT/p2g/room"
    const val BASE_WS_URL_USER = "ws://$HOST:$PORT/p2g/user"

}
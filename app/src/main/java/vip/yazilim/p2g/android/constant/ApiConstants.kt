package vip.yazilim.p2g.android.constant

/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
object ApiConstants {

    //    private const val HOST = "142.93.239.61"
    private const val HOST = "192.168.1.150"
    private const val PORT = "8080"
    const val BASE_URL = "http://$HOST:$PORT"
    const val BASE_API_URL = "http://$HOST:$PORT/api/"
    private const val BASE_WS_URL = "ws://$HOST:$PORT/p2g/"
    const val BASE_WS_URL_ROOM = "${BASE_WS_URL}room/"
    const val BASE_WS_URL_USER = "${BASE_WS_URL}user/"
    const val SPOTIFY_BASE_URL = "https://accounts.spotify.com"

}
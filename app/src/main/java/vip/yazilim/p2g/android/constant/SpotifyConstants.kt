package vip.yazilim.p2g.android.constant

/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
object SpotifyConstants {

    const val AUTH_TOKEN_REQUEST_CODE = 0x10
    const val CLIENT_ID = "76f07bcdc1054c32884054e43a135480"
    const val REDIRECT_URI = "play2gether://"
    val SCOPE = arrayOf(
        "user-modify-playback-state",
        "user-read-email",
        "user-read-playback-state",
        "user-read-private"
    )

}
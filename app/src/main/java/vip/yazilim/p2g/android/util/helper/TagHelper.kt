package vip.yazilim.p2g.android.util.helper

/**
 * @author mustafaarifsisman - 27.02.2020
 * @contact mustafaarifsisman@gmail.com
 */

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

const val REQUEST_TAG = "Play2GetherRequest"
const val SPOTIFY_REQUEST_TAG = "SpotifyRequest"
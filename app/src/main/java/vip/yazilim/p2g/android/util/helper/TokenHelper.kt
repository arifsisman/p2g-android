package vip.yazilim.p2g.android.util.helper

import vip.yazilim.p2g.android.util.data.SharedPrefSingleton

/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
object TokenHelper {
    fun getAccessTokenFromSharedPref(): String? {
        return SharedPrefSingleton.read(
            "access_token",
            null
        )
    }
}
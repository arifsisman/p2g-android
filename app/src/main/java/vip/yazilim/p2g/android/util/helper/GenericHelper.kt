package vip.yazilim.p2g.android.util.helper

import vip.yazilim.p2g.android.BuildConfig

/**
 * @author mustafaarifsisman - 27.02.2020
 * @contact mustafaarifsisman@gmail.com
 */

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

//inline fun <T, R> with(receiver: T, block: T.() -> R) {}

inline fun release(code: () -> Unit) {
    if (BuildConfig.BUILD_TYPE == "release") code()
}

inline fun debug(code: () -> Unit) {
    if (BuildConfig.BUILD_TYPE == "debug") code()
}
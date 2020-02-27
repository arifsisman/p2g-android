package vip.yazilim.p2g.android.util.helper

/**
 * @author mustafaarifsisman - 27.02.2020
 * @contact mustafaarifsisman@gmail.com
 */

inline fun <reified T> T.TAG(): String = T::class.java.simpleName
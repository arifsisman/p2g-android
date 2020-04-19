package vip.yazilim.p2g.android.api.generic

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
interface Callback<T> {
    fun onSuccess(obj: T)
    fun onError(msg: String)
}

//fun <T> callback(cb: (obj: T?, msg: String?) -> Unit): Callback<T> = object : Callback<T> {
//    override fun onSuccess(obj: T) = cb(obj, null)
//    override fun onError(msg: String) = cb(null, msg)
//}

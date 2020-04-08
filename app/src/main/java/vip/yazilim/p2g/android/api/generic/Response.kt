package vip.yazilim.p2g.android.api.generic

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class Response<E>(
    val timestamp: Long,
    val path: String,
    val hasError: Boolean,
    val message: String,
    val data: E
)
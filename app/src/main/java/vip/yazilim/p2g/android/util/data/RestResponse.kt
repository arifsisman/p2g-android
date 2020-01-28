package vip.yazilim.p2g.android.util.data

data class RestResponse<E>(
    val timestamp: Long?,
    val path: String?,
    val hasError: Boolean?,
    val message: String?,
    val data: E?
)
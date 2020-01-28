package vip.yazilim.p2g.android.api.generic

data class Response<E>(
    val timestamp: Long?,
    val path: String?,
    val hasError: Boolean?,
    val message: String?,
    val data: E?
)
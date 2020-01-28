package vip.yazilim.p2g.android.api.generic

data class P2GResponse<E>(
    val timestamp: Long?,
    val path: String?,
    val hasError: Boolean?,
    val message: String?,
    val data: E?
)
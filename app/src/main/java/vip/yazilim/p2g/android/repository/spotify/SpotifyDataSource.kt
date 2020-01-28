package vip.yazilim.p2g.android.repository.spotify

import vip.yazilim.p2g.android.util.data.OperationCallback

interface SpotifyDataSource {
    fun getTokens(code: String, callback: OperationCallback)
    fun refreshTokens(refreshToken: String, callback: OperationCallback)
    fun cancel()
}
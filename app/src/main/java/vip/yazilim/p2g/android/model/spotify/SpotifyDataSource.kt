package vip.yazilim.p2g.android.model.spotify

import vip.yazilim.p2g.android.data.OperationCallback

interface SpotifyDataSource {
    fun getTokens(code: String, callback: OperationCallback)
    fun refreshTokens(refreshToken: String, callback: OperationCallback)
    fun cancel()
}
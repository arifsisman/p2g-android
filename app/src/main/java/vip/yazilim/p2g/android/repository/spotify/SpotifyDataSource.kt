package vip.yazilim.p2g.android.repository.spotify

import vip.yazilim.p2g.android.api.generic.Callback

interface SpotifyDataSource {
    fun getTokens(code: String, callback: Callback)
    fun refreshTokens(refreshToken: String, callback: Callback)
    fun cancel()
}
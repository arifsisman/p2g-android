package vip.yazilim.p2g.android.util.refrofit

import vip.yazilim.p2g.android.api.Play2GetherWebApi
import vip.yazilim.p2g.android.api.SpotifyWebApi
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.client.SpotifyApiClient

/**
 * @author mustafaarifsisman - 18.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
object Singleton {
    private var spotifyWebApi: SpotifyWebApi = SpotifyApiClient.build()
    private var webApi: Play2GetherWebApi = ApiClient.build()

    fun initApis() {
        spotifyWebApi = SpotifyApiClient.build()
        webApi = ApiClient.build()
    }

    fun apiClient(): Play2GetherWebApi {
        return webApi
    }

    fun spotifyApiClient(): SpotifyWebApi {
        return spotifyWebApi
    }

    fun buildApi() {
        webApi = ApiClient.build()
    }

}
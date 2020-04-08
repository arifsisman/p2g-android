package vip.yazilim.p2g.android.util.refrofit

import vip.yazilim.p2g.android.api.Play2GetherWebApi
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.generic.request

/**
 * @author mustafaarifsisman - 18.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
object Singleton {
    private lateinit var webApi: Play2GetherWebApi

    fun apiClient(): Play2GetherWebApi = webApi

    fun buildApi(accessToken: String) {
        webApi = ApiClient.build(accessToken)
        request(webApi.updateAccessToken(accessToken), null)
    }

}
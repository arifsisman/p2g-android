package vip.yazilim.p2g.android.api.spotify

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import vip.yazilim.p2g.android.model.p2g.User
import vip.yazilim.p2g.android.util.data.RestResponse

/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */

interface AuthorizationApi {

    @GET("/api/spotify/login")
    fun login(): Call<RestResponse<User>>

    @POST("/api/spotify/token")
    fun updateAccessToken(@Body accessToken:String): Call<RestResponse<String>>

}
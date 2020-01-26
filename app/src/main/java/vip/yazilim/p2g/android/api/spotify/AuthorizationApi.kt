package vip.yazilim.p2g.android.api.spotify

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import vip.yazilim.p2g.android.data.p2g.User

/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */

interface AuthorizationApi {

    @GET("spotify/login")
    fun login(): Call<User>

    @POST("spotify/token")
    fun updateAccessToken(@Body accessToken:String): Call<String>

}
package vip.yazilim.p2g.android.api.p2g

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import vip.yazilim.p2g.android.model.p2g.User
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.util.data.RestResponse

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
interface UserApi {

    @GET("/api/user/{id}")
    fun getUser(@Path("id") userId: String): Call<RestResponse<User>>

    @PUT("/api/user/")
    fun updateUser(@Body user: User): Call<RestResponse<User>>

    @GET("/api/user/{id}/model")
    fun getUserModel(@Path("id") userId: String): Call<RestResponse<UserModel>>

}
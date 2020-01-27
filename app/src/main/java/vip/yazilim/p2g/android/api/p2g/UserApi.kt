package vip.yazilim.p2g.android.api.p2g

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import vip.yazilim.p2g.android.model.p2g.User
import vip.yazilim.p2g.android.model.p2g.UserModel

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
interface UserApi {

    @GET("user/{id}")
    fun getUser(@Path("id") userId: String): Call<User>

    @PUT("user/")
    fun updateUser(@Body user:User): Call<User>

    @GET("user/{id}/model")
    fun getUserModel(@Path("id") userId: String): Call<UserModel>

}
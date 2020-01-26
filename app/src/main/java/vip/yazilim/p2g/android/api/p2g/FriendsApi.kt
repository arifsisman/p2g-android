package vip.yazilim.p2g.android.api.p2g

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import vip.yazilim.p2g.android.data.p2g.User

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
interface FriendsApi {

    @POST("friend/requests/")
    fun getRequests(): Call<List<User>>

    @GET("friend/requests/{id}")
    fun getRequestById(@Path("id") friendRequestId: Long): Call<List<User>>

    @POST("friend/requests/{userId}/send")
    fun send(@Path("userId") userId: String): Call<Boolean>

    @PUT("friend/requests/{id}/accept")
    fun accept(@Path("id") friendRequestId: Long): Call<Boolean>

    @PUT("friend/requests/{id}/reject")
    fun reject(@Path("id") friendRequestId: Long): Call<Boolean>

    @PUT("friend/requests/{id}/ignore")
    fun ignore(@Path("id") friendRequestId: Long): Call<Boolean>

}
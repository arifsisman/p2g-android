package vip.yazilim.p2g.android.api.p2g

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import vip.yazilim.p2g.android.model.p2g.User
import vip.yazilim.p2g.android.util.data.RestResponse

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
interface FriendsApi {

    @POST("/api/friend/requests/")
    fun getRequests(): Call<RestResponse<List<User>>>

    @GET("/api/friend/requests/{id}")
    fun getRequestById(@Path("id") friendRequestId: Long): Call<RestResponse<List<User>>>

    @POST("/api/friend/requests/{userId}/send")
    fun send(@Path("userId") userId: String): Call<RestResponse<Boolean>>

    @PUT("/api/friend/requests/{id}/accept")
    fun accept(@Path("id") friendRequestId: Long): Call<RestResponse<Boolean>>

    @PUT("/api/friend/requests/{id}/reject")
    fun reject(@Path("id") friendRequestId: Long): Call<RestResponse<Boolean>>

    @PUT("/api/friend/requests/{id}/ignore")
    fun ignore(@Path("id") friendRequestId: Long): Call<RestResponse<Boolean>>

}
package vip.yazilim.p2g.android.api.p2g

import retrofit2.Call
import retrofit2.http.*
import vip.yazilim.p2g.android.data.p2g.Room
import vip.yazilim.p2g.android.data.p2g.RoomInvite
import vip.yazilim.p2g.android.data.p2g.RoomUser
import vip.yazilim.p2g.android.data.p2g.User
import vip.yazilim.p2g.android.data.p2g.model.RoomModel

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
interface RoomApi {

    @POST("room/")
    fun createRoom(@Body room: Room): Call<Room>

    @GET("room/{id}")
    fun getRoom(@Path("id") roomId: Long): Call<Room>

    @GET("room/")
    fun getAllRoom(): Call<List<Room>>

    @PUT("room/")
    fun updateRoom(@Body room: Room): Call<Room>

    @DELETE("room/{id}")
    fun deleteRoom(@Path("id") roomId: Long): Call<Boolean>

    @GET("room/{id}/model")
    fun getRoomModel(@Path("id") roomId: Long): Call<RoomModel>

    @POST("room/{roomId}/invite/{userId}")
    fun inviteUser(@Path("roomId") roomId: Long, @Path("userId") userId: String): Call<RoomInvite>

    @POST("room/invite/accept")
    fun acceptInvite(@Body roomInvite: RoomInvite): Call<RoomUser>

    @DELETE("room/invite/{id}/reject")
    fun rejectInvite(@Path("id") roomId: Long): Call<Boolean>

    @POST("room/{id}/join")
    fun joinRoom(@Path("id") roomId: Long, @Body password: String): Call<RoomUser>

    @DELETE("room/leave")
    fun leaveRoom(): Call<Boolean>

    @GET("room/{id}/users")
    fun getRoomUsers(@Path("id") roomId: Long): Call<List<User>>

    @PUT("room/user/{id}/promote")
    fun promoteUser(@Path("id") roomId: String): Call<RoomUser>

    @PUT("room/user/{id}/demote")
    fun demoteUser(@Path("id") roomId: String): Call<RoomUser>

}
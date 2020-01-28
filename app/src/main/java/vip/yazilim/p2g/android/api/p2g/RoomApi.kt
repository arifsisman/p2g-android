package vip.yazilim.p2g.android.api.p2g

import retrofit2.Call
import retrofit2.http.*
import vip.yazilim.p2g.android.model.p2g.*
import vip.yazilim.p2g.android.util.data.RestResponse

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
interface RoomApi {

    @POST("/api/room/")
    fun createRoom(@Body room: Room): Call<RestResponse<Room>>

    @GET("/api/room/{id}")
    fun getRoom(@Path("id") roomId: Long): Call<RestResponse<Room>>

    @GET("/api/room/")
    fun getAllRooms(): Call<List<RestResponse<Room>>>

    @GET("/api/room/model/")
    fun getRoomModels(): Call<RestResponse<List<RoomModel>>>

    @PUT("/apiroom/")
    fun updateRoom(@Body room: Room): Call<RestResponse<Room>>

    @DELETE("/api/room/{id}")
    fun deleteRoom(@Path("id") roomId: Long): Call<RestResponse<Boolean>>

    @GET("/api/room/model/{id}")
    fun getRoomModel(@Path("id") roomId: Long): Call<RestResponse<RoomModel>>

    @POST("/api/room/{roomId}/invite/{userId}")
    fun inviteUser(@Path("roomId") roomId: Long, @Path("userId") userId: String): Call<RestResponse<RoomInvite>>

    @POST("/api/room/invite/accept")
    fun acceptInvite(@Body roomInvite: RoomInvite): Call<RestResponse<RoomUser>>

    @DELETE("/api/room/invite/{id}/reject")
    fun rejectInvite(@Path("id") roomId: Long): Call<RestResponse<Boolean>>

    @POST("/api/room/{id}/join")
    fun joinRoom(@Path("id") roomId: Long, @Body password: String): Call<RestResponse<RoomUser>>

    @DELETE("/api/room/leave")
    fun leaveRoom(): Call<RestResponse<Boolean>>

    @GET("/api/room/{id}/users")
    fun getRoomUsers(@Path("id") roomId: Long): Call<RestResponse<List<User>>>

    @PUT("/api/room/user/{id}/promote")
    fun promoteUser(@Path("id") roomId: String): Call<RestResponse<RoomUser>>

    @PUT("/api/room/user/{id}/demote")
    fun demoteUser(@Path("id") roomId: String): Call<RestResponse<RoomUser>>

}
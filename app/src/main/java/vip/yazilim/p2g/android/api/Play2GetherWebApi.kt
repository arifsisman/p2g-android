package vip.yazilim.p2g.android.api

import retrofit2.Call
import retrofit2.http.*
import vip.yazilim.p2g.android.api.generic.Response
import vip.yazilim.p2g.android.model.p2g.*

/**
 * @author mustafaarifsisman - 28.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
interface Play2GetherWebApi {

    // User API
    @GET("/api/user/{id}")
    fun getUser(@Path("id") userId: String): Call<Response<User>>

    @PUT("/api/user/")
    fun updateUser(@Body user: User): Call<Response<User>>

    @GET("/api/user/{id}/model")
    fun getUserModel(@Path("id") userId: String): Call<Response<UserModel>>

    @GET("/api/user/me/model")
    fun getUserModelMe(): Call<Response<UserModel>>


    // Room API
    @POST("/api/room/create/{roomName}")
    fun createRoom(@Path("roomName") roomName: String, @Body roomPassword: String): Call<Response<Room>>

    @GET("/api/room/{id}")
    fun getRoom(@Path("id") roomId: Long): Call<Response<Room>>

    @GET("/api/room/")
    fun getAllRooms(): Call<List<Response<Room>>>

    @GET("/api/room/model/")
    fun getSimplifiedRoomModels(): Call<Response<MutableList<RoomModelSimplified>>>

    @PUT("/api/room/")
    fun updateRoom(@Body room: Room): Call<Response<Room>>

    @DELETE("/api/room/{id}")
    fun deleteRoom(@Path("id") roomId: Long): Call<Response<Boolean>>

    @GET("/api/room/model/{id}")
    fun getRoomModel(@Path("id") roomId: Long): Call<Response<RoomModel>>

    @GET("/api/room/model/me")
    fun getRoomModelMe(): Call<Response<RoomModel>>

    @GET("/api/room/smodel/{id}")
    fun getSimplifiedRoomModel(@Path("id") roomId: Long): Call<Response<RoomModelSimplified>>

    @POST("/api/room/{roomId}/invite/{userId}")
    fun inviteUser(@Path("roomId") roomId: Long, @Path("userId") userId: String): Call<Response<RoomInvite>>

    @POST("/api/room/invite/accept")
    fun acceptInvite(@Body roomInvite: RoomInvite): Call<Response<RoomUser>>

    @DELETE("/api/room/invite/{id}/reject")
    fun rejectInvite(@Path("id") roomId: Long): Call<Response<Boolean>>

    @POST("/api/room/{id}/join")
    fun joinRoom(@Path("id") roomId: Long, @Body password: String): Call<Response<RoomUser>>

    @DELETE("/api/room/leave")
    fun leaveRoom(): Call<Response<Boolean>>

    @GET("/api/room/{id}/users")
    fun getRoomUsers(@Path("id") roomId: Long): Call<Response<List<User>>>

    @PUT("/api/room/user/{id}/promote")
    fun promoteUser(@Path("id") roomId: String): Call<Response<RoomUser>>

    @PUT("/api/room/user/{id}/demote")
    fun demoteUser(@Path("id") roomId: String): Call<Response<RoomUser>>

    @GET("/api/room/invite/model")
    fun getRoomInviteModels(): Call<Response<MutableList<RoomInviteModel>>>

    @GET("/api/room/user/me")
    fun getRoomUserMe(): Call<Response<RoomUser>>

    // Song API
    @GET("/api/song/{id}")
    fun getSong(@Path("id") songId: Long): Call<Response<Song>>

    @GET("/api/song/{roomId}/list")
    fun getRoomSongs(@Path("roomId") roomId: Long): Call<Response<MutableList<Song>>>

    @POST("/api/song/{roomId}")
    fun addSongToRoom(@Path("roomId") roomId: Long, @Body searchModelList: List<SearchModel>): Call<Response<Boolean>>

    @DELETE("/api/song/{songId}")
    fun removeSongFromRoom(@Path("songId") songId: Long): Call<Response<Boolean>>

    @PUT("/api/song/{songId}/upvote")
    fun upvoteSong(@Path("songId") songId: Long): Call<Response<Int>>

    @PUT("/api/song/{songId}/downvote")
    fun downvoteSong(@Path("songId") songId: Long): Call<Response<Int>>


    // Friends API
    @POST("/api/friend/requests/")
    fun getRequests(): Call<Response<List<User>>>

    @GET("/api/friend/requests/{id}")
    fun getRequestById(@Path("id") friendRequestId: Long): Call<Response<List<User>>>

    @POST("/api/friend/requests/{userId}/add")
    fun addFriend(@Path("userId") userId: String): Call<Response<Boolean>>

    @DELETE("/api/friend/requests/{userId}/delete")
    fun deleteFriend(@Path("userId") userId: String): Call<Response<Boolean>>

    @PUT("/api/friend/requests/{id}/accept")
    fun accept(@Path("id") friendRequestId: Long): Call<Response<Boolean>>

    @PUT("/api/friend/requests/{id}/reject")
    fun reject(@Path("id") friendRequestId: Long): Call<Response<Boolean>>

    @PUT("/api/friend/requests/{id}/ignore")
    fun ignore(@Path("id") friendRequestId: Long): Call<Response<Boolean>>

    @GET("/api/friend/requests/model")
    fun getFriendRequestModel(): Call<Response<MutableList<FriendRequestModel>>>

    @GET("/api/friend/requests/{userId}/model")
    fun getFriendRequestModel(@Path("userId") userId: String): Call<Response<MutableList<FriendRequestModel>>>

    @GET("/api/friend/requests/friends")
    fun getFriends(): Call<Response<MutableList<FriendModel>>>

    @GET("/api/friend/requests/{userId}/friends")
    fun getFriends(@Path("userId") userId: String): Call<Response<MutableList<FriendModel>>>

    @GET("/api/friend/requests/friends/counts")
    fun getFriendsCounts(): Call<Response<Int>>

    @GET("/api/friend/requests/{userId}/friends/counts")
    fun getFriendsCounts(@Path("userId") userId: String): Call<Response<Int>>


    // Authorization API
    @GET("/api/spotify/login")
    fun login(): Call<Response<User>>

    @POST("/api/spotify/logout")
    fun logout(): Call<Response<Boolean>>

    @POST("/api/spotify/token")
    fun updateAccessToken(@Body accessToken: String): Call<Response<String>>


    // Player API
    @POST("/api/spotify/player/play")
    fun play(@Body song: Song): Call<Response<List<Song>>>

    @POST("/api/spotify/player/{id}/play")
    fun startResume(@Path("id") roomId: Long): Call<Response<List<Song>>>

    @POST("/api/spotify/player/{id}/pause")
    fun pause(@Path("id") roomId: Long): Call<Response<List<Song>>>

    @POST("/api/spotify/player/{id}/next")
    fun next(@Path("id") roomId: Long): Call<Response<List<Song>>>

    @POST("/api/spotify/player/{id}/previous")
    fun previous(@Path("id") roomId: Long): Call<Response<List<Song>>>

    @POST("/api/spotify/player/{id}/seek/{ms}")
    fun seek(@Path("id") roomId: Long, @Path("ms") ms: Int): Call<Response<Int>>

    @POST("/api/spotify/player/{id}/repeat")
    fun repeat(@Path("id") roomId: Long): Call<Response<Boolean>>


    // Spotify API
    @GET("/api/spotify/search/{query}")
    fun search(@Path("query") query: String): Call<Response<MutableList<SearchModel>>>

}
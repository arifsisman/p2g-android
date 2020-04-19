package vip.yazilim.p2g.android.api

import retrofit2.Call
import retrofit2.http.*
import vip.yazilim.p2g.android.entity.*
import vip.yazilim.p2g.android.model.p2g.*

/**
 * @author mustafaarifsisman - 28.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
interface Endpoints {

    // User API
    @GET("/api/user/{id}")
    fun getUser(@Path("id") userId: String): Call<User>

    @GET("/api/user/")
    fun getAllUsers(): Call<RestResponse<MutableList<User>>>

    @GET("/api/user/{id}/model")
    fun getUserModel(@Path("id") userId: String): Call<RestResponse<UserModel>>

    @GET("/api/user/me/model")
    fun getUserModelMe(): Call<RestResponse<UserModel>>


    // Room API
    @POST("/api/room/create/{roomName}")
    fun createRoom(
        @Path("roomName") roomName: String,
        @Body roomPassword: String
    ): Call<RestResponse<RoomUserModel>>

    @GET("/api/room/model/")
    fun getRoomModels(): Call<RestResponse<MutableList<RoomModel>>>

    @PUT("/api/room/")
    fun updateRoom(@Body room: Room): Call<RestResponse<Room>>

    @GET("/api/room/model/{id}")
    fun getRoomModel(@Path("id") roomId: Long): Call<RestResponse<RoomModel>>

    @GET("/api/room/model/me")
    fun getRoomModelMe(): Call<RestResponse<RoomUserModel>>


    // Room Invite API
    @POST("/api/room/{roomId}/invite/{userId}")
    fun inviteUser(
        @Path("roomId") roomId: Long,
        @Path("userId") userId: String
    ): Call<RestResponse<RoomInvite>>

    @POST("/api/room/invite/accept")
    fun acceptInvite(@Body roomInvite: RoomInvite): Call<RestResponse<RoomUserModel>>

    @DELETE("/api/room/invite/{id}")
    fun rejectInvite(@Path("id") roomId: Long): Call<RestResponse<Boolean>>

    @GET("/api/room/invite/model")
    fun getRoomInviteModels(): Call<RestResponse<MutableList<RoomInviteModel>>>


    // Room User API
    @POST("/api/room/{id}/join")
    fun joinRoom(
        @Path("id") roomId: Long,
        @Body password: String
    ): Call<RestResponse<RoomUserModel>>

    @GET("/api/room/{id}/users")
    fun getRoomUsers(@Path("id") roomId: Long): Call<RestResponse<List<User>>>

    @GET("/api/room/{id}/roomUserModels")
    fun getRoomUserModels(@Path("id") roomId: Long): Call<RestResponse<MutableList<RoomUserModel>>>

    @DELETE("/api/room/user/leave")
    fun leaveRoom(): Call<RestResponse<Boolean>>

    @PUT("/api/room/user/{id}/changeRole")
    fun changeRoomUserRole(
        @Path("id") roomUserId: Long,
        @Body roleName: String
    ): Call<RestResponse<RoomUser>>

    @GET("/api/room/user/me")
    fun getRoomUserModelMe(): Call<RestResponse<RoomUserModel>>

    // Room Queue API
    @GET("/api/room/{roomId}/queue")
    fun getRoomSongs(@Path("roomId") roomId: Long): Call<RestResponse<MutableList<Song>>>

    @POST("/api/room/{roomId}/queue")
    fun addSongWithSearchModel(
        @Path("roomId") roomId: Long,
        @Body searchModel: SearchModel
    ): Call<RestResponse<MutableList<Song>>>

    @DELETE("/api/room/{roomId}/queue")
    fun clearQueue(@Path("roomId") roomId: Long): Call<RestResponse<Boolean>>

    @DELETE("/api/room/queue/{songId}/remove")
    fun removeSongFromRoom(@Path("songId") songId: Long): Call<RestResponse<Boolean>>

    @PUT("/api/room/queue/{songId}/upvote")
    fun upvoteSong(@Path("songId") songId: Long): Call<RestResponse<Int>>

    @PUT("/api/room/queue/{songId}/downvote")
    fun downvoteSong(@Path("songId") songId: Long): Call<RestResponse<Int>>


    // User Friends API
    @POST("/api/user/{userId}/add")
    fun addFriend(@Path("userId") userId: String): Call<RestResponse<Boolean>>

    @DELETE("/api/user/{userId}/delete")
    fun deleteFriend(@Path("userId") userId: String): Call<RestResponse<Boolean>>

    @GET("/api/user/me/friends/model")
    fun getUserFriendModel(): Call<RestResponse<UserFriendModel>>

    @GET("/api/user/me/friends")
    fun getFriends(): Call<RestResponse<MutableList<User>>>

    @GET("/api/user/me/friends/counts")
    fun getFriendsCounts(): Call<RestResponse<Int>>

    @GET("/api/user/{userId}/friends/counts")
    fun getFriendsCounts(@Path("userId") userId: String): Call<RestResponse<Int>>

    @PUT("/api/user/friends/{id}/accept")
    fun accept(@Path("id") friendRequestId: Long): Call<RestResponse<Boolean>>

    @PUT("/api/user/friends/{id}/reject")
    fun reject(@Path("id") friendRequestId: Long): Call<RestResponse<Boolean>>

    @PUT("/api/user/friends/{id}/ignore")
    fun ignore(@Path("id") friendRequestId: Long): Call<RestResponse<Boolean>>

    @GET("/api/user/search/name/{query}")
    fun searchUser(@Path("query") query: String): Call<RestResponse<MutableList<User>>>


    // Spotify Authorization API
    @GET("/api/spotify/login")
    fun login(): Call<RestResponse<User>>

    @POST("/api/spotify/logout")
    fun logout(): Call<RestResponse<Boolean>>

    @PUT("/api/spotify/token")
    fun updateAccessToken(@Body accessToken: String): Call<RestResponse<String>>


    // Spotify Player API
    @POST("/api/spotify/room/play")
    fun play(@Body song: Song): Call<RestResponse<Boolean>>

    @POST("/api/spotify/room/{id}/playPause")
    fun playPause(@Path("id") roomId: Long): Call<RestResponse<Boolean>>

    @POST("/api/spotify/room/{id}/next")
    fun next(@Path("id") roomId: Long): Call<RestResponse<Boolean>>

    @POST("/api/spotify/room/{id}/previous")
    fun previous(@Path("id") roomId: Long): Call<RestResponse<Boolean>>

    @POST("/api/spotify/room/{id}/seek/{ms}")
    fun seek(@Path("id") roomId: Long, @Path("ms") ms: Int): Call<RestResponse<Boolean>>

    @POST("/api/spotify/room/{id}/repeat")
    fun repeat(@Path("id") roomId: Long): Call<RestResponse<Boolean>>

    @POST("/api/spotify/room/sync")
    fun syncWithRoom(): Call<RestResponse<Boolean>>


    // Spotify Search API
    @GET("/api/spotify/search/{query}")
    fun searchSpotify(@Path("query") query: String): Call<RestResponse<MutableList<SearchModel>>>

    @GET("/api/spotify/search/recommendations")
    fun getRecommendations(): Call<RestResponse<MutableList<SearchModel>>>

    // Spotify Device API
    @GET("/api/spotify/user/device")
    fun getUserDevices(): Call<RestResponse<MutableList<UserDevice>>>

    @PUT("/api/spotify/user/device")
    fun saveUsersActiveDevice(@Body userDevice: UserDevice): Call<RestResponse<UserDevice>>
}
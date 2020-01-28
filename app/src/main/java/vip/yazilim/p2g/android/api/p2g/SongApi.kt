package vip.yazilim.p2g.android.api.p2g

import retrofit2.Call
import retrofit2.http.*
import vip.yazilim.p2g.android.model.p2g.SearchModel
import vip.yazilim.p2g.android.model.p2g.Song
import vip.yazilim.p2g.android.util.data.RestResponse

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
interface SongApi {

    @GET("/api/song/{id}")
    fun getSong(@Path("id") songId: Long): Call<RestResponse<Song>>

    @GET("/api/song/{roomId}/list")
    fun getRoomSongList(@Path("roomId") roomId: Long): Call<RestResponse<List<Song>>>

    @POST("/api/song/{roomId}")
    fun addSongToRoom(@Path("roomId") roomId: Long, @Body searchModelList: List<SearchModel>): Call<RestResponse<List<Song>>>

    @DELETE("/api/song/{songId}")
    fun removeSongFromRoom(@Path("songId") songId: Long): Call<RestResponse<List<Song>>>

    @PUT("/api/song/{songId}/upvote")
    fun upvoteSong(@Path("songId") songId: Long): Call<RestResponse<Int>>

    @PUT("/api/song/{songId}/downvote")
    fun downvoteSong(@Path("songId") songId: Long): Call<RestResponse<Int>>

}
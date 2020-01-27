package vip.yazilim.p2g.android.api.p2g

import retrofit2.Call
import retrofit2.http.*
import vip.yazilim.p2g.android.data.p2g.Song
import vip.yazilim.p2g.android.data.p2g.model.SearchModel

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
interface SongApi {

    @GET("song/{id}")
    fun getSong(@Path("id") songId: Long): Call<Song>

    @GET("song/{roomId}/list")
    fun getRoomSongList(@Path("roomId") roomId: Long): Call<List<Song>>

    @POST("song/{roomId}")
    fun addSongToRoom(@Path("roomId") roomId: Long, @Body searchModelList: List<SearchModel>): Call<List<Song>>

    @DELETE("song/{songId}")
    fun removeSongFromRoom(@Path("songId") songId: Long): Call<List<Song>>

    @PUT("song/{songId}/upvote")
    fun upvoteSong(@Path("songId") songId: Long): Call<Int>

    @PUT("song/{songId}/downvote")
    fun downvoteSong(@Path("songId") songId: Long): Call<Int>

}
package vip.yazilim.p2g.android.api.spotify

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import vip.yazilim.p2g.android.model.p2g.Song
import vip.yazilim.p2g.android.util.data.RestResponse

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
interface SpotifyPlayerApi {

    @POST("/api/spotify/player/play")
    fun play(@Body song: Song): Call<RestResponse<List<Song>>>

    @POST("/api/spotify/player/{id}/play")
    fun startResume(@Path("id") roomId: Long): Call<RestResponse<List<Song>>>

    @POST("/api/spotify/player/{id}/pause")
    fun pause(@Path("id") roomId: Long): Call<RestResponse<List<Song>>>

    @POST("/api/spotify/player/{id}/next")
    fun next(@Path("id") roomId: Long): Call<RestResponse<List<Song>>>

    @POST("/api/spotify/player/{id}/previous")
    fun previous(@Path("id") roomId: Long): Call<RestResponse<List<Song>>>

    @POST("/api/spotify/player/{id}/seek/{ms}")
    fun seek(@Path("id") roomId: Long, @Path("ms") ms: Int): Call<RestResponse<Int>>

    @POST("/api/spotify/player/{id}/repeat")
    fun repeat(@Path("id") roomId: Long): Call<RestResponse<Boolean>>

}
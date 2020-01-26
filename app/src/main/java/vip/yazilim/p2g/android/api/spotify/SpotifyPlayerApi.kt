package vip.yazilim.p2g.android.api.spotify

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import vip.yazilim.p2g.android.data.p2g.Song

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
interface SpotifyPlayerApi {

    @POST("spotify/player/play")
    fun play(@Body song: Song): Call<List<Song>>

    @POST("spotify/player/{id}/play")
    fun startResume(@Path("id") roomId: Long): Call<List<Song>>

    @POST("spotify/player/{id}/pause")
    fun pause(@Path("id") roomId: Long): Call<List<Song>>

    @POST("spotify/player/{id}/next")
    fun next(@Path("id") roomId: Long): Call<List<Song>>

    @POST("spotify/player/{id}/previous")
    fun previous(@Path("id") roomId: Long): Call<List<Song>>

    @POST("spotify/player/{id}/seek/{ms}")
    fun seek(@Path("id") roomId: Long, @Path("ms") ms: Int): Call<Int>

    @POST("spotify/player/{id}/repeat")
    fun repeat(@Path("id") roomId: Long): Call<Boolean>

}
package vip.yazilim.p2g.android.api.p2g

import retrofit2.Call
import retrofit2.http.GET
import vip.yazilim.p2g.android.dto.User

/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */

interface LoginService {

    @GET("spotify/login")
    fun login(): Call<User>

}
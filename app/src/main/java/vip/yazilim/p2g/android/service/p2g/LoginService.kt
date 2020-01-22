package vip.yazilim.p2g.android.service.p2g

import android.content.Context
import retrofit2.Response
import vip.yazilim.p2g.android.api.p2g.spotify.LoginApi
import vip.yazilim.p2g.android.model.p2g.User
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton
import vip.yazilim.p2g.android.util.helper.UIHelper
import vip.yazilim.p2g.android.util.rest.RetrofitClient

/**
 * @author mustafaarifsisman - 22.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
object LoginService {
    fun loginToPlay2Gether(context: Context, accessToken: String) {
        RetrofitClient.getClient(accessToken).create(LoginApi::class.java).login()
            .enqueue(object : retrofit2.Callback<User> {

                override fun onResponse(call: retrofit2.Call<User>, response: Response<User>) {
                    val user = response.body()!!
                    SharedPrefSingleton.write("id", user.id)
                    SharedPrefSingleton.write("email", user.email)
                    SharedPrefSingleton.write("name", user.name)
                    SharedPrefSingleton.write("image_url", user.imageUrl)
                }

                override fun onFailure(call: retrofit2.Call<User>?, t: Throwable?) {
                    UIHelper.showToastLong(context, "Failed to login Play2Gether")
                }
            }

            )
    }
}
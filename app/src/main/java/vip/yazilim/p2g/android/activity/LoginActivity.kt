package vip.yazilim.p2g.android.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.Call
import retrofit2.Response
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.p2g.spotify.AuthorizationApi
import vip.yazilim.p2g.android.api.spotify.SpotifyTokenApi
import vip.yazilim.p2g.android.constant.SharedPreferencesConstants
import vip.yazilim.p2g.android.constant.SpotifyConstants
import vip.yazilim.p2g.android.model.p2g.User
import vip.yazilim.p2g.android.model.spotify.TokenModel
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton
import vip.yazilim.p2g.android.util.helper.UIHelper
import vip.yazilim.p2g.android.util.rest.RetrofitClient

/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class LoginActivity : AppCompatActivity() {

    private var mCall: Call? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPrefSingleton.init(this, SharedPreferencesConstants.INFO)
        setContentView(R.layout.activity_login)

        if (SharedPrefSingleton.contains("access_token")!!) {
//            startMainActivity()
        }

        spotify_login_btn.setOnClickListener {
            loginToSpotify()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (SpotifyConstants.AUTH_TOKEN_REQUEST_CODE == requestCode) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            val accessToken = response.accessToken
            SharedPrefSingleton.write("access_token", accessToken)
            loginToPlay2Gether(accessToken)
        } else if (SpotifyConstants.AUTH_CODE_REQUEST_CODE == requestCode) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            val code = response.code
            getTokensFromSpotifyAndLogin(code)
        }
    }


    override fun onDestroy() {
        cancelCall()
        super.onDestroy()
    }

    private fun cancelCall() {
        if (mCall != null) {
            mCall!!.cancel()
        }
    }



    private fun loginToSpotify() {
        val request: AuthenticationRequest = AuthenticationRequest.Builder(
            SpotifyConstants.CLIENT_ID,
            AuthenticationResponse.Type.CODE,
            SpotifyConstants.REDIRECT_URI
        )
            .setShowDialog(false)
            .setScopes(SpotifyConstants.SCOPE)
            .build()

        AuthenticationClient.openLoginActivity(
            this,
            SpotifyConstants.AUTH_CODE_REQUEST_CODE,
            request
        )
    }

    private fun loginToPlay2Gether(accessToken: String) {
        RetrofitClient.getClient(accessToken).create(AuthorizationApi::class.java).login()
            .enqueue(object : retrofit2.Callback<User> {

                override fun onResponse(call: retrofit2.Call<User>, response: Response<User>) {
                    val user = response.body()!!
                    SharedPrefSingleton.write("id", user.id)
                    SharedPrefSingleton.write("email", user.email)
                    SharedPrefSingleton.write("name", user.name)
                    SharedPrefSingleton.write("image_url", user.imageUrl)
                    startMainActivity()
                }

                override fun onFailure(call: retrofit2.Call<User>?, t: Throwable?) {
                    UIHelper.showToastLong(this@LoginActivity, "Failed to login Play2Gether")
                }
            }

            )
    }

    private fun getTokensFromSpotifyAndLogin(code: String) {
        RetrofitClient.getSpotifyClient().create(SpotifyTokenApi::class.java)
            .getTokens(
                SpotifyConstants.CLIENT_ID,
                SpotifyConstants.CLIENT_SECRET,
                "authorization_code",
                code,
                SpotifyConstants.REDIRECT_URI
            )
            .enqueue(object : retrofit2.Callback<TokenModel> {

                override fun onResponse(
                    call: retrofit2.Call<TokenModel>,
                    model: Response<TokenModel>
                ) {
                    val tokenModel: TokenModel = model.body()!!
                    SharedPrefSingleton.write("access_token", tokenModel.access_token)
                    SharedPrefSingleton.write("refresh_token", tokenModel.refresh_token)

                    loginToPlay2Gether(tokenModel.access_token)
                }

                override fun onFailure(
                    call: retrofit2.Call<TokenModel>?,
                    t: Throwable?
                ) {
                    UIHelper.showToastLong(this@LoginActivity, "Failed to login Spotify")
                }
            }

            )
    }

    private fun startMainActivity() {
        val myIntent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(myIntent)
    }

}

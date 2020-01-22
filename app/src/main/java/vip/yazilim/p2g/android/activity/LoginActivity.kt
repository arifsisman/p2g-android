package vip.yazilim.p2g.android.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.Call
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.p2g.spotify.LoginApi
import vip.yazilim.p2g.android.api.spotify.AuthorizationApi
import vip.yazilim.p2g.android.constant.SharedPreferencesConstants
import vip.yazilim.p2g.android.constant.SpotifyConstants
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton
import vip.yazilim.p2g.android.util.helper.UIHelper
import vip.yazilim.p2g.android.util.refrofit.Result
import vip.yazilim.p2g.android.util.refrofit.RetrofitClient
import vip.yazilim.p2g.android.util.refrofit.enqueue


/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class LoginActivity : AppCompatActivity() {

    private var mCall: Call? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPrefSingleton.init(this, SharedPreferencesConstants.INFO)
        Logger.addLogAdapter(AndroidLogAdapter())
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
            val accessToken = getTokensFromSpotify(code)
            loginToPlay2Gether(accessToken)
        }

        startMainActivity()
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
        val request: AuthenticationRequest = AuthenticationRequest
            .Builder(
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

    private fun startMainActivity() {
        val myIntent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(myIntent)
    }

    private fun loginToPlay2Gether(accessToken: String) {
        RetrofitClient.getClient(accessToken).create(LoginApi::class.java).login()
            .enqueue { result ->
                when (result) {
                    is Result.Success -> {
                        val user = result.response.body()
                        SharedPrefSingleton.write("id", user?.id)
                        SharedPrefSingleton.write("email", user?.email)
                        SharedPrefSingleton.write("name", user?.name)
                        SharedPrefSingleton.write("image_url", user?.imageUrl)
                    }
                    is Result.Failure -> {
                        Log.d("Play2Gether", result.error.toString())
                        UIHelper.showToastLong(applicationContext, "Failed to login Play2Gether")
                    }
                }
            }
    }

    private fun getTokensFromSpotify(code: String): String {
        RetrofitClient.getSpotifyClient().create(AuthorizationApi::class.java)
            .getTokens(
                SpotifyConstants.CLIENT_ID,
                SpotifyConstants.CLIENT_SECRET,
                SpotifyConstants.GRANT_TYPE_AUTHORIZATION_CODE_REQUEST,
                code,
                SpotifyConstants.REDIRECT_URI
            ).enqueue { result ->
                when (result) {
                    is Result.Success -> {
                    val tokenModel = result.response.body()!!
                    SharedPrefSingleton.write("access_token", tokenModel.access_token)
                    SharedPrefSingleton.write("refresh_token", tokenModel.refresh_token)
                    }
                    is Result.Failure -> {
                        Log.d("Play2Gether", result.error.toString())
                        UIHelper.showToastLong(applicationContext, "Failed to login Spotify")
                    }
                }
            }
        return SharedPrefSingleton.read("access_token", null).toString()
    }
}

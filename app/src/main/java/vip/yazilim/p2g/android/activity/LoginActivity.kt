package vip.yazilim.p2g.android.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.Call
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.SharedPreferencesConstants
import vip.yazilim.p2g.android.constant.SpotifyConstants
import vip.yazilim.p2g.android.service.p2g.LoginService.loginToPlay2Gether
import vip.yazilim.p2g.android.service.spotify.AuthorizationService
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton


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
            loginToPlay2Gether(applicationContext, accessToken)
        } else if (SpotifyConstants.AUTH_CODE_REQUEST_CODE == requestCode) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            val code = response.code
            val accessToken = AuthorizationService.getTokensFromSpotify(applicationContext, code)
            loginToPlay2Gether(applicationContext, accessToken)
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

    private fun startMainActivity() {
        val myIntent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(myIntent)
    }

}

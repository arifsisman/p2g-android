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
import vip.yazilim.p2g.android.data.p2g.User
import vip.yazilim.p2g.android.data.spotify.TokenModel
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton
import vip.yazilim.p2g.android.util.helper.DBHelper
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
    val db by lazy { DBHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPrefSingleton.init(this, SharedPreferencesConstants.INFO)
        Logger.addLogAdapter(AndroidLogAdapter())
        setContentView(R.layout.activity_login)

        spotify_login_btn.setOnClickListener {
            getAuthorizationCodeFromSpotify()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // If the authorization code has been received successfully
        if (SpotifyConstants.AUTH_CODE_REQUEST_CODE == requestCode) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            getTokensFromSpotify(response.code)
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

    // getAuthorizationCodeFromSpotify via Spotify Android SDK
    private fun getAuthorizationCodeFromSpotify() {
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

    // getTokensFromSpotify via Spotify Web API
    private fun getTokensFromSpotify(code: String) {
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
                        if (result.response.isSuccessful) {
                            val tokenModel = result.response.body()!!
                            SharedPrefSingleton.write("access_token", tokenModel.access_token)
                            SharedPrefSingleton.write("refresh_token", tokenModel.refresh_token)
                            loginToPlay2Gether(tokenModel)
                        } else {
                            val errorMessage = result.response.errorBody()!!.string()
                            Log.d("Play2Gether", errorMessage)
                            UIHelper.showErrorDialog(this, errorMessage)
                        }
                    }
                    is Result.Failure -> {
                        Log.d("Play2Gether", result.error.toString())
                        UIHelper.showToastLong(applicationContext, "Failed to login Spotify")
                    }
                }
            }
    }

    // loginToPlay2Gether via Play2Gether Web API
    private fun loginToPlay2Gether(tokenModel: TokenModel) {
        RetrofitClient.getClient(tokenModel.access_token).create(LoginApi::class.java).login()
            .enqueue { result ->
                when (result) {
                    is Result.Success -> {
                        if (result.response.isSuccessful) {
                            val user = result.response.body()

                            if (user != null) {
                                db.insertData(user)
                            }

                            startMainActivity(user, tokenModel)
                        } else {
                            val errorMessage = result.response.errorBody()!!.string()
                            Log.d("Play2Gether", errorMessage)
                            UIHelper.showErrorDialog(this, errorMessage)
                        }
                    }
                    is Result.Failure -> {
                        Log.d("Play2Gether", result.error.toString())
                        UIHelper.showToastLong(applicationContext, "Failed to login Play2Gether")
                    }
                }
            }
    }

    private fun startMainActivity(user: User?, tokenModel: TokenModel) {
        val startMainIntent = Intent(this@LoginActivity, MainActivity::class.java)
        startMainIntent.putExtra("user", user)
        startMainIntent.putExtra("tokenModel", tokenModel)
        startActivity(startMainIntent)
    }

}

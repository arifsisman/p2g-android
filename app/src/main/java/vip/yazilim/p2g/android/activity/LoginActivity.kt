package vip.yazilim.p2g.android.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.threetenabp.AndroidThreeTen
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import okhttp3.Call
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.api.generic.spotifyRequest
import vip.yazilim.p2g.android.constant.SharedPreferencesConstants
import vip.yazilim.p2g.android.constant.SpotifyConstants
import vip.yazilim.p2g.android.constant.TokenConstants
import vip.yazilim.p2g.android.entity.User
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.model.spotify.TokenModel
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton
import vip.yazilim.p2g.android.util.helper.TAG
import vip.yazilim.p2g.android.util.helper.UIHelper
import vip.yazilim.p2g.android.util.refrofit.Singleton


/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class LoginActivity : AppCompatActivity() {

    private var mCall: Call? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        // Init DB and AndroidThreeTen
        AndroidThreeTen.init(this)
        SharedPrefSingleton.init(this, SharedPreferencesConstants.INFO)

        getAuthorizationCodeFromSpotify()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // If the authorization code has been received successfully
        if (SpotifyConstants.AUTH_CODE_REQUEST_CODE == requestCode) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            if (response.code != null) {
                getTokensFromSpotify(response.code)
            } else {
                val msg = "Can not get authorization code from Spotify"
                Log.d(TAG, msg)
                UIHelper.showToastShort(this, msg)
            }
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
    private fun getTokensFromSpotify(code: String) =
        spotifyRequest(Singleton.spotifyApiClient().getTokens(
            SpotifyConstants.CLIENT_ID,
            SpotifyConstants.CLIENT_SECRET,
            SpotifyConstants.GRANT_TYPE_AUTHORIZATION_CODE_REQUEST,
            code,
            SpotifyConstants.REDIRECT_URI
        ), object : Callback<TokenModel> {
            override fun onError(msg: String) {
                UIHelper.showToastLong(this@LoginActivity, msg)
            }

            override fun onSuccess(obj: TokenModel) {
                SharedPrefSingleton.write(TokenConstants.ACCESS_TOKEN, obj.access_token)
                SharedPrefSingleton.write(TokenConstants.REFRESH_TOKEN, obj.refresh_token)
                Singleton.initApis()
                loginToPlay2Gether(obj)
            }
        })

    // loginToPlay2Gether via Play2Gether Web API
    private fun loginToPlay2Gether(tokenModel: TokenModel) = request(
        Singleton.apiClient().login(),
        object : Callback<User> {
            override fun onError(msg: String) {
                val alert = UIHelper.showErrorDialog(this@LoginActivity, msg)
                alert?.setOnCancelListener {
                    getAuthorizationCodeFromSpotify()
                }
            }

            override fun onSuccess(obj: User) {
                checkIsUserInRoom(obj, tokenModel)
            }
        })


    private fun checkIsUserInRoom(user: User, tokenModel: TokenModel) = request(
        Singleton.apiClient().getRoomModelMe(),
        object : Callback<RoomModel> {
            override fun onSuccess(obj: RoomModel) {
                val roomIntent = Intent(this@LoginActivity, RoomActivity::class.java)
                roomIntent.putExtra("roomModel", obj)
                startActivity(roomIntent)
            }

            override fun onError(msg: String) {
                UIHelper.showToastLong(this@LoginActivity, "Logged in as ${user.name}")
                val startMainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                startMainIntent.putExtra("user", user)
                startMainIntent.putExtra("tokenModel", tokenModel)
                startActivity(startMainIntent)
            }
        })

}

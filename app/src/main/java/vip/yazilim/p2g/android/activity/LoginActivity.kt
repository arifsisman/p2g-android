package vip.yazilim.p2g.android.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.threetenabp.AndroidThreeTen
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.Call
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.client.SpotifyApiClient
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.P2GRequest
import vip.yazilim.p2g.android.api.generic.SpotifyRequest
import vip.yazilim.p2g.android.constant.ErrorConstants.SPOTIFY_PRODUCT_TYPE_ERROR
import vip.yazilim.p2g.android.constant.GeneralConstants.LOG_TAG
import vip.yazilim.p2g.android.constant.GeneralConstants.PREMIUM_PRODUCT_TYPE
import vip.yazilim.p2g.android.constant.SharedPreferencesConstants
import vip.yazilim.p2g.android.constant.SpotifyConstants
import vip.yazilim.p2g.android.constant.TokenConstants
import vip.yazilim.p2g.android.model.p2g.User
import vip.yazilim.p2g.android.model.spotify.TokenModel
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton
import vip.yazilim.p2g.android.util.helper.UIHelper


/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class LoginActivity : AppCompatActivity() {

    private var mCall: Call? = null
//    private val db by lazy { DBHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Init DB and AndroidThreeTen
        AndroidThreeTen.init(this)
        SharedPrefSingleton.init(this, SharedPreferencesConstants.INFO)

        spotify_login_btn.setOnClickListener {
            getAuthorizationCodeFromSpotify()
        }

        spotify_login_btn.performClick()
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
                Log.d(LOG_TAG, msg)
                UIHelper.showToastShort(this, msg)
//                UIHelper.showSnackBarLong(findViewById(android.R.id.content), msg)
            }
        }
    }

//    override fun startActivity(intent: Intent?) {
//        super.startActivity(intent)
//        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out)
//    }
//
//    override fun finish() {
//        super.finish()
//        overridePendingTransition(R.anim.from_left_in, R.anim.from_right_out)
//    }

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
        SpotifyRequest.build(
            SpotifyApiClient.build().getTokens(
                SpotifyConstants.CLIENT_ID,
                SpotifyConstants.CLIENT_SECRET,
                SpotifyConstants.GRANT_TYPE_AUTHORIZATION_CODE_REQUEST,
                code,
                SpotifyConstants.REDIRECT_URI
            ), object : Callback<TokenModel> {
                override fun onError(msg: String) {
                    Log.d(LOG_TAG, msg)
                    UIHelper.showToastLong(this@LoginActivity, msg)
//                    UIHelper.showSnackBarLong(findViewById(android.R.id.content), msg)
                }

                override fun onSuccess(obj: TokenModel) {
                    SharedPrefSingleton.write(TokenConstants.ACCESS_TOKEN, obj.access_token)
                    SharedPrefSingleton.write(TokenConstants.REFRESH_TOKEN, obj.refresh_token)
//                    db.insertData(obj)
                    loginToPlay2Gether(obj)
                }
            }
        )
    }

    // loginToPlay2Gether via Play2Gether Web API
    private fun loginToPlay2Gether(tokenModel: TokenModel) {
        P2GRequest.build(ApiClient.build().login(),
            object : Callback<User> {
                override fun onError(msg: String) {
                    Log.d(LOG_TAG, msg)
                    UIHelper.showErrorDialog(this@LoginActivity, msg)
                }

                override fun onSuccess(obj: User) {
                    if (obj.spotifyProductType != PREMIUM_PRODUCT_TYPE) {
                        UIHelper.showToastLong(this@LoginActivity, SPOTIFY_PRODUCT_TYPE_ERROR)
//                        UIHelper.showSnackBarLong(findViewById(android.R.id.content), SPOTIFY_PRODUCT_TYPE_ERROR)

                    } else {
//                        db.insertData(obj)
                        UIHelper.showToastLong(this@LoginActivity, "Logged in as ${obj.name}")
//                        UIHelper.showSnackBarLong(findViewById(android.R.id.content), "Logged in as ${obj.name}")
                        startMainActivity(obj, tokenModel)
                    }
                }
            })
    }

    private fun startMainActivity(user: User, tokenModel: TokenModel) {
        val startMainIntent = Intent(this@LoginActivity, MainActivity::class.java)
        startMainIntent.putExtra("user", user)
        startMainIntent.putExtra("tokenModel", tokenModel)
        startActivity(startMainIntent)
    }

}

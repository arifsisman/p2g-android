package vip.yazilim.p2g.android.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.jakewharton.threetenabp.AndroidThreeTen
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import okhttp3.Call
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.constant.SharedPreferencesConstants
import vip.yazilim.p2g.android.constant.SpotifyConstants
import vip.yazilim.p2g.android.entity.User
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.util.data.SharedPrefSingleton
import vip.yazilim.p2g.android.util.helper.TAG
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showErrorDialog
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showToastLong
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showToastShort


/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class LoginActivity : AppCompatActivity() {

    private var mCall: Call? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        MobileAds.initialize(this)
        supportActionBar?.hide()

        // Init DB and AndroidThreeTen
        AndroidThreeTen.init(this)
        SharedPrefSingleton.init(this, SharedPreferencesConstants.INFO)

        getAccessTokenFromSpotify()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // If the authorization code has been received successfully
        if (SpotifyConstants.AUTH_TOKEN_REQUEST_CODE == requestCode) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            if (response.accessToken != null) {
                ApiClient.buildApi(response.accessToken)
                loginToPlay2Gether()
            } else {
                val msg = resources.getString(R.string.err_authorization_code)
                Log.d(TAG, msg)
                this.showToastShort(msg)
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

    private fun getAccessTokenFromSpotify() {
        val request: AuthenticationRequest = AuthenticationRequest
            .Builder(
                SpotifyConstants.CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                SpotifyConstants.REDIRECT_URI
            )
            .setShowDialog(false)
            .setScopes(SpotifyConstants.SCOPE)
            .build()

        AuthenticationClient.openLoginActivity(
            this,
            SpotifyConstants.AUTH_TOKEN_REQUEST_CODE,
            request
        )
    }

    // loginToPlay2Gether via Play2Gether Web API
    private fun loginToPlay2Gether() = request(
        ApiClient.get().login(),
        object : Callback<User> {
            override fun onError(msg: String) {
                val alert = this@LoginActivity.showErrorDialog(msg)
                alert?.setOnCancelListener { getAccessTokenFromSpotify() }
            }

            override fun onSuccess(obj: User) {
                SharedPrefSingleton.write("userName", obj.name)
                SharedPrefSingleton.write("userId", obj.id)
                checkIsUserInRoom(obj)
            }
        })


    private fun checkIsUserInRoom(user: User) = request(
        ApiClient.get().getRoomModelMe(),
            object : Callback<RoomModel> {
                override fun onSuccess(obj: RoomModel) {
                    val roomIntent = Intent(this@LoginActivity, RoomActivity::class.java)
                    roomIntent.putExtra("roomModel", obj)
                    startActivity(roomIntent)
                }

                override fun onError(msg: String) {
                    val info = resources.getString(R.string.info_logged_in)
                    this@LoginActivity.showToastLong("$info ${user.name}")
                    val startMainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                    startMainIntent.putExtra("user", user)
                    startActivity(startMainIntent)
                }
            })

}

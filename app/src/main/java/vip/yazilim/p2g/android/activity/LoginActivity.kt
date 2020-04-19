package vip.yazilim.p2g.android.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.jakewharton.threetenabp.AndroidThreeTen
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import okhttp3.Call
import vip.yazilim.p2g.android.Play2GetherApplication
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.api.Api.then
import vip.yazilim.p2g.android.constant.SpotifyConstants
import vip.yazilim.p2g.android.entity.User
import vip.yazilim.p2g.android.util.helper.TAG
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showErrorDialog
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showToastLong
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showToastShort


/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class LoginActivity : BaseActivity() {

    private var mCall: Call? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        MobileAds.initialize(this)
        supportActionBar?.hide()

        // Init DB and AndroidThreeTen
        AndroidThreeTen.init(this)
        getAccessTokenFromSpotify()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // If the access token has been received successfully
        if (SpotifyConstants.AUTH_TOKEN_REQUEST_CODE == requestCode) {
            val response = AuthorizationClient.getResponse(resultCode, data)
            if (response.accessToken != null) {
                Api.build(response.accessToken)
//                Api.client.login().withCallback(
//                    callback { obj, msg ->
//                        msg?.let {
//                            this@LoginActivity.showErrorDialog(it, ::getAccessTokenFromSpotify)
//                        }
//                        obj?.let {
//                            Play2GetherApplication.userName = obj.name
//                            Play2GetherApplication.userId = obj.id
//                            getUserModel(obj)
//                        }
//                    }
//                )

                Api.client.login() then { obj, msg ->
                    obj?.let {
                        Play2GetherApplication.userName = it.name
                        Play2GetherApplication.userId = it.id
                        getUserModel(it)
                    }
                    msg?.let {
                        this@LoginActivity.showErrorDialog(it, ::getAccessTokenFromSpotify)
                    }
                }

            } else {
                response.error.let {
                    Log.d(TAG, it)
                    this.showToastShort(it)
                }
            }
        }
    }

    override fun handleUnauthorizedEvent() {
        //Don't handle unauthorized event
    }

    override fun onDestroy() {
        mCall?.cancel()
        super.onDestroy()
    }

    private fun getUserModel(user: User) {
        Api.client.getUserModelMe() then { obj, msg ->
            obj?.let {
                if (obj.roomModel == null) {
                    this@LoginActivity.showToastLong("${resources.getString(R.string.info_logged_in)} ${user.name}")
                    val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                    mainIntent.putExtra("user", user)
                    startActivity(mainIntent)
                } else {
                    val roomIntent = Intent(this@LoginActivity, RoomActivity::class.java)
                    roomIntent.putExtra("room", obj.roomModel!!.room)
                    startActivity(roomIntent)
                }
            }
            msg?.let {
                this@LoginActivity.showErrorDialog(msg)
            }
        }
    }

    fun getAccessTokenFromSpotify() {
        val request: AuthorizationRequest = AuthorizationRequest
            .Builder(
                SpotifyConstants.CLIENT_ID,
                AuthorizationResponse.Type.TOKEN,
                SpotifyConstants.REDIRECT_URI
            )
            .setShowDialog(true)
            .setScopes(SpotifyConstants.SCOPE)
            .build()

        AuthorizationClient.openLoginActivity(
            this@LoginActivity,
            SpotifyConstants.AUTH_TOKEN_REQUEST_CODE,
            request
        )
    }
}

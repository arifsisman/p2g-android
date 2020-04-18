package vip.yazilim.p2g.android.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.jakewharton.threetenabp.AndroidThreeTen
import com.spotify.sdk.android.auth.AuthorizationClient
import okhttp3.Call
import vip.yazilim.p2g.android.Play2GetherApplication
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.api.Api.withCallback
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.constant.SpotifyConstants
import vip.yazilim.p2g.android.entity.User
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.util.helper.SpotifyHelper.Companion.getAccessTokenFromSpotify
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
        Play2GetherApplication.currentActivity = this
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
                Api.client.login().withCallback(
                    object : Callback<User> {
                        override fun onError(msg: String) {
                            val alert = this@LoginActivity.showErrorDialog(msg)
                            alert?.setOnCancelListener { getAccessTokenFromSpotify() }
                        }

                        override fun onSuccess(obj: User) {
                            Play2GetherApplication.user = obj
                            checkIsUserInRoom(obj)
                        }
                    })
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

    private fun checkIsUserInRoom(user: User) = Api.client.getUserModelMe().withCallback(
        object : Callback<UserModel> {
            //user in room
            override fun onSuccess(obj: UserModel) {
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

            //user not in room
            override fun onError(msg: String) {

            }
        })
}

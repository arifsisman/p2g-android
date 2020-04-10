package vip.yazilim.p2g.android.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.util.helper.SpotifyHelper.Companion.getAccessTokenFromSpotify
import vip.yazilim.p2g.android.util.helper.TAG
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
                            Play2GetherApplication.userId = obj.id
                            Play2GetherApplication.userName = obj.name
                            checkIsUserInRoom(obj)
                        }
                    })
            } else {
                val msg = resources.getString(R.string.err_authorization_code)
                Log.d(TAG, msg)
                this.showToastShort(msg)
            }
        }
    }

    override fun handleUnauthorizedEvent() {
        //Don't handle unauthorized event
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

    private fun checkIsUserInRoom(user: User) = Api.client.getRoomModelMe().withCallback(
        object : Callback<RoomModel> {
            //user in room
            override fun onSuccess(obj: RoomModel) {
                val roomIntent = Intent(this@LoginActivity, RoomActivity::class.java)
                roomIntent.putExtra("roomModel", obj)
                startActivity(roomIntent)
            }

            //user not in room
            override fun onError(msg: String) {
                val info = resources.getString(R.string.info_logged_in)
                this@LoginActivity.showToastLong("$info ${user.name}")
                val startMainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                startMainIntent.putExtra("user", user)
                startActivity(startMainIntent)
            }
        })

    fun Activity.showErrorDialog(message: String): AlertDialog? {
        if (!this.isFinishing) {
            val dialogBuilder = MaterialAlertDialogBuilder(this)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("Error")
            alert.show()

            return alert
        }

        return null
    }
}

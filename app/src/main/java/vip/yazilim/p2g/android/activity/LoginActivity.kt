package vip.yazilim.p2g.android.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.p2g.LoginService
import vip.yazilim.p2g.android.constant.SharedPreferencesConstants
import vip.yazilim.p2g.android.constant.SpotifyConstants
import vip.yazilim.p2g.android.dto.User
import vip.yazilim.p2g.android.util.network.RetrofitClient
import java.io.IOException

/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class LoginActivity : AppCompatActivity() {

    private var spotifyAccessToken: String? = null
    private var mCall: Call? = null
    private val mOkHttpClient = OkHttpClient()
    private lateinit var prefences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        prefences =
            getSharedPreferences(SharedPreferencesConstants.SPOTIFY_INFO, Context.MODE_PRIVATE)

        val accessToken = prefences.getString("access_token", null)

        //TODO: open
//        if (accessToken != null) {
//            startMainActivity()
//        }

        spotify_login_btn.setOnClickListener {
            val request = getAuthenticationRequest(AuthenticationResponse.Type.TOKEN)

            AuthenticationClient.openLoginActivity(
                this,
                SpotifyConstants.AUTH_TOKEN_REQUEST_CODE,
                request
            )
        }
    }

    private fun getAuthenticationRequest(type: AuthenticationResponse.Type): AuthenticationRequest {
        return AuthenticationRequest.Builder(
            SpotifyConstants.CLIENT_ID,
            type,
            SpotifyConstants.REDIRECT_URI
        )
            .setShowDialog(false)
            .setScopes(SpotifyConstants.SCOPE)
            .build()
    }


    private fun fetchSpotifyUserProfile() {
        Log.d("Status: ", "Please Wait...")
        if (spotifyAccessToken == null) {
            Log.i("Status: ", "Something went wrong - No Access Token found")
            return
        }

        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .addHeader("Authorization", "Bearer " + spotifyAccessToken!!)
            .build()

        cancelCall()
        mCall = mOkHttpClient.newCall(request)

        mCall!!.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("Status: ", "Failed to fetch data: $e")
                Toast.makeText(applicationContext, "Failed to login", Toast.LENGTH_SHORT).show()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: okhttp3.Response) {
                try {
                    val jsonObject = JSONObject(response.body!!.string())

                    Log.d("Status: ", "Success get all JSON ${jsonObject.toString(3)}")
                    saveUserSpotifyInfo(jsonObject)

                    // Play2Gether Login
                    loginToP2G()

                    // start main activity
                    startMainActivity()

                    finish()
                } catch (e: JSONException) {
                    Log.d("Status: ", "Failed to parse data: $e")
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (SpotifyConstants.AUTH_TOKEN_REQUEST_CODE == requestCode) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            spotifyAccessToken = response.accessToken
            fetchSpotifyUserProfile()
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

    private fun startMainActivity() {
        val myIntent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(myIntent)
    }

    private fun saveUserSpotifyInfo(jsonObject: JSONObject) {
        val editor = prefences.edit()

        val spotifyId = jsonObject.getString("id")
        val spotifyEmail = jsonObject.getString("email")
        val spotifyDisplayName = jsonObject.getString("display_name")
        val spotifyProfileImage = jsonObject.getJSONArray("images")
        val spotifyAccessToken = spotifyAccessToken
        var spotifyImageURL = ""
        if (spotifyProfileImage.length() > 0) {
            spotifyImageURL = spotifyProfileImage.getJSONObject(0).getString("url")
        }

        editor.putString("id", spotifyId)
        editor.putString("email", spotifyEmail)
        editor.putString("display_name", spotifyDisplayName)
        editor.putString("id", spotifyId)
        editor.putString("images", spotifyImageURL)
        editor.putString("access_token", spotifyAccessToken)

        editor.apply()
    }

    private fun loginToP2G() {
        val accessToken = prefences.getString("access_token", null)
        println("accessToken:$accessToken")

        RetrofitClient.getClient(accessToken)
            .create(LoginService::class.java)
            .login()
            .enqueue(object : retrofit2.Callback<User> {

                override fun onResponse(
                    call: retrofit2.Call<User>,
                    response: Response<User>
                ) {
                    val user = response.body()
                    Toast.makeText(
                        this@LoginActivity,
                        "Succesfuly login to p2g",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailure(call: retrofit2.Call<User>?, t: Throwable?) {
                    Toast.makeText(this@LoginActivity, "Failure", Toast.LENGTH_SHORT).show()
                }
            }

            )
    }

}

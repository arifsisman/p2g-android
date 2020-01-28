package vip.yazilim.p2g.android.model.spotify

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vip.yazilim.p2g.android.constant.GeneralConstants.LOG_TAG
import vip.yazilim.p2g.android.constant.SpotifyConstants
import vip.yazilim.p2g.android.data.OperationCallback
import vip.yazilim.p2g.android.data.SpotifyApiClient

class SpotifyRepository : SpotifyDataSource {

    private var call: Call<TokenModel>? = null

    override fun getTokens(code: String, callback: OperationCallback) {
        call = SpotifyApiClient.build()?.getTokens(
            SpotifyConstants.CLIENT_ID,
            SpotifyConstants.CLIENT_SECRET,
            SpotifyConstants.GRANT_TYPE_AUTHORIZATION_CODE_REQUEST,
            code,
            SpotifyConstants.REDIRECT_URI
        )
        call?.enqueue(object : Callback<TokenModel> {
            override fun onFailure(call: Call<TokenModel>, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<TokenModel>, response: Response<TokenModel>) {
                response.body()?.let {
                    if (response.isSuccessful) {
                        Log.v(LOG_TAG, "data $it")
                        callback.onSuccess(it)
                    } else {
                        callback.onError(it)
                    }
                }
            }
        })
    }

    override fun refreshTokens(refreshToken: String, callback: OperationCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancel() {
        call?.cancel()
    }
}
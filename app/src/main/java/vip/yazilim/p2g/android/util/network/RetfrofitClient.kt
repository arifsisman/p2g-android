package vip.yazilim.p2g.android.util.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */


class RetrofitClient{
    companion object {

        fun getClient():Retrofit{
            return Retrofit.Builder()
                    //TODO: change
                .baseUrl("https://www.simplifiedcoding.net/demos/")
                .addConverterFactory(GsonConverterFactory.create()).build()
        }
    }

}
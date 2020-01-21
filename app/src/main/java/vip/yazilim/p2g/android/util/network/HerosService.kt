package vip.yazilim.p2g.android.util.network

import retrofit2.Call
import retrofit2.http.GET
import vip.yazilim.p2g.android.dto.Hero

interface HerosService {
    @GET("marvel")
    fun getHeroes():Call<List<Hero>>

}
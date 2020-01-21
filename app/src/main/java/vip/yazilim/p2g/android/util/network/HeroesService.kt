package vip.yazilim.p2g.android.util.network

import vip.yazilim.p2g.android.dto.Hero
import retrofit2.Call
import retrofit2.http.GET

interface HeroesService {
    @GET("marvel")
    fun getHeroes():Call<List<Hero>>

}
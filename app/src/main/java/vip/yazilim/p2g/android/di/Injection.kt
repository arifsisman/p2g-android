package vip.yazilim.p2g.android.di

import vip.yazilim.p2g.android.model.RoomDataSource
import vip.yazilim.p2g.android.model.RoomRepository
import vip.yazilim.p2g.android.model.spotify.SpotifyDataSource
import vip.yazilim.p2g.android.model.spotify.SpotifyRepository

object Injection {

    fun roomProviderRepository():RoomDataSource{
        return RoomRepository()
    }

    fun spotifyProviderRepository():SpotifyDataSource{
        return SpotifyRepository()
    }
}
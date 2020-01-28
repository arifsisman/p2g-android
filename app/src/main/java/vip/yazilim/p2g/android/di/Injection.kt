package vip.yazilim.p2g.android.di

import vip.yazilim.p2g.android.repository.p2g.RoomDataSource
import vip.yazilim.p2g.android.repository.p2g.RoomRepository
import vip.yazilim.p2g.android.repository.spotify.SpotifyDataSource
import vip.yazilim.p2g.android.repository.spotify.SpotifyRepository

object Injection {

    fun roomProviderRepository(): RoomDataSource {
        return RoomRepository()
    }

    fun spotifyProviderRepository(): SpotifyDataSource {
        return SpotifyRepository()
    }
}
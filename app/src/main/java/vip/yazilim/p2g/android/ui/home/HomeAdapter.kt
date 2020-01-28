package vip.yazilim.p2g.android.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.model.p2g.RoomModel

class HomeAdapter(private var roomModels: List<RoomModel>) :
    RecyclerView.Adapter<HomeAdapter.MViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_home, parent, false)
        return MViewHolder(view)
    }

    override fun onBindViewHolder(vh: MViewHolder, position: Int) {
        val roomModel = roomModels[position]

        vh.roomName.text = roomModel.room.name
        val ownerText = "Room Owner: " + roomModel.owner.name
        vh.owner.text = ownerText

        roomModel.songList?.forEach {
            when (it.songStatus) {
                SongStatus.PLAYING.songStatus -> {
                    val nowPlayingText = "Now Playing: " + it.songName + " - " + it.artistNames[0]
                    vh.nowPlaying.text = nowPlayingText
                    return
                }
                SongStatus.PAUSED.songStatus -> {
                    val nowPlayingText = "Paused Song: " + it.songName + " - " + it.artistNames[0]
                    vh.nowPlaying.text = nowPlayingText
                    return
                }
                SongStatus.NEXT.songStatus -> {
                    val nowPlayingText = "Next Song: " + it.songName + " - " + it.artistNames[0]
                    vh.nowPlaying.text = nowPlayingText
                    return
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return roomModels.size
    }

    fun update(data: List<RoomModel>) {
        roomModels = data
        notifyDataSetChanged()
    }

    class MViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roomName: TextView = view.findViewById(R.id.room_name)
        val owner: TextView = view.findViewById(R.id.room_owner)
        val nowPlaying: TextView = view.findViewById(R.id.room_now_playing)
    }
}
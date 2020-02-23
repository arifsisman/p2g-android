package vip.yazilim.p2g.android.ui.room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.model.p2g.Song
import vip.yazilim.p2g.android.util.glide.GlideApp
import vip.yazilim.p2g.android.util.helper.RoomHelper

/**
 * @author mustafaarifsisman - 23.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class PlayerAdapter(private var songOnPlayer: MutableList<Song>) :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {
    private lateinit var view: View

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songName: TextView = itemView.findViewById(R.id.song_name)
        private val songArtists: TextView = itemView.findViewById(R.id.song_artists)
        private val songImage: ImageView = itemView.findViewById(R.id.song_image)
        private val seekBar: SeekBar = itemView.findViewById(R.id.seek_bar)

        fun bindViewMinimized(song: Song) {
            songName.text = song.songName
            songArtists.text = RoomHelper.getArtistsPlaceholder(song.artistNames, "")

            if (song.imageUrl != null && !song.imageUrl!!.contains("mp3")) {
                GlideApp.with(view)
                    .load(song.imageUrl)
                    .into(songImage)
            } else {
                songImage.setImageResource(R.mipmap.ic_launcher)
            }

            //TODO seekbar
        }

        private val songNameExp: TextView = itemView.findViewById(R.id.song_name)
        private val songArtistsExp: TextView = itemView.findViewById(R.id.song_artists)
        private val songImageExp: ImageView = itemView.findViewById(R.id.song_image)
        private val seekBarExp: SeekBar = itemView.findViewById(R.id.seek_bar_exp)
        private val songCurrent: TextView = itemView.findViewById(R.id.song_current)
        private val songMax: TextView = itemView.findViewById(R.id.song_max)

        fun bindViewExpanded(song: Song) {
            songNameExp.text = song.songName
            songArtistsExp.text = RoomHelper.getArtistsPlaceholder(song.artistNames, "")

            if (song.imageUrl != null && !song.imageUrl!!.contains("mp3")) {
                GlideApp.with(view)
                    .load(song.imageUrl)
                    .into(songImageExp)
            } else {
                songImageExp.setImageResource(R.mipmap.ic_launcher)
            }

            //TODO seekbar and song ms
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bindViewMinimized(songOnPlayer[0])
        holder.bindViewExpanded(songOnPlayer[0])

//        songOnPlayer.forEach {
//            when (it.songStatus) {
//                SongStatus.PLAYING.songStatus -> {
//                    songOnPlayer = it
//                    updateSongOnPlayer(songOnPlayer)
//                    return@forEach
//                }
//                SongStatus.PAUSED.songStatus -> {
//                    songOnPlayer = it
//                    updateSongOnPlayer(songOnPlayer)
//                    return@forEach
//                }
//                SongStatus.NEXT.songStatus -> {
//                    songOnPlayer = it
//                    updateSongOnPlayer(songOnPlayer)
//                    return@forEach
//                }
//            }
//        }
    }

    fun updateSongOnPlayer(data: MutableList<Song>) {
        songOnPlayer = data
        notifyDataSetChanged()
    }

}
package vip.yazilim.p2g.android.ui.room.roomqueue

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.model.p2g.Song
import vip.yazilim.p2g.android.util.glide.GlideApp
import vip.yazilim.p2g.android.util.helper.RoomHelper

/**
 * @author mustafaarifsisman - 20.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomQueueAdapter(
    var songs: MutableList<Song>
) : RecyclerView.Adapter<RoomQueueAdapter.MViewHolder>() {

    private lateinit var view: View

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songName: TextView = itemView.findViewById(R.id.song_name)
        private val songArtists: TextView = itemView.findViewById(R.id.song_artists)
        private val songImage: ImageView = itemView.findViewById(R.id.song_image)
        private val songVote: TextView = itemView.findViewById(R.id.song_vote)

        fun bindView(song: Song) {
            songName.text = song.songName
            songArtists.text = RoomHelper.getArtistsPlaceholder(song.artistNames, "")

            if (song.imageUrl != null) {
                GlideApp.with(view)
                    .load(song.imageUrl)
                    .into(songImage)
            } else {
                songImage.setImageResource(R.mipmap.ic_launcher)
            }

            when {
                song.votes > 0 -> {
                    val songVotePlaceholder = "+" + song.votes.toString()
                    songVote.text = songVotePlaceholder
                    songVote.setTextColor(Color.parseColor("#1DB954"))
                }
                song.votes < 0 -> {
                    val songVotePlaceholder = "-" + song.votes.toString()
                    songVote.text = songVotePlaceholder
                    songVote.setTextColor(Color.parseColor("#B91D1D"))
                }
                else -> {
                    val songVotePlaceholder = "0"
                    songVote.text = songVotePlaceholder
                    songVote.setTextColor(Color.parseColor("#808080"))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.row_room_queue, parent, false)
        return MViewHolder(view)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.bindView(songs[position])
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun getItemId(position: Int): Long {
        return songs[position].id
    }

    fun update(data: MutableList<Song>) {
        songs = data.filter { it.songStatus != SongStatus.PLAYED.songStatus }.toMutableList()
        notifyDataSetChanged()
    }

    fun add(song: Song, position: Int) {
        songs.add(position, song)
        notifyItemInserted(position)
    }

    fun clear() {
        songs.clear()
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        val size = songs.size
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, size)

        songs.removeAt(position)
    }

    fun remove(song: Song) {
        val position = songs.indexOf(song)
        val size = songs.size
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, size)

        songs.remove(song)
    }

}
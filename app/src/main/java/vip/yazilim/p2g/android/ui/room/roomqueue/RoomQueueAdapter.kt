package vip.yazilim.p2g.android.ui.room.roomqueue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.model.p2g.Song
import vip.yazilim.p2g.android.util.glide.GlideApp
import vip.yazilim.p2g.android.util.helper.RoomHelper

/**
 * @author mustafaarifsisman - 20.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomQueueAdapter(
    private var songs: MutableList<Song>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RoomQueueAdapter.MViewHolder>() {

    private lateinit var view: View

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songName: TextView = itemView.findViewById(R.id.song_name)
        private val songArtists: TextView = itemView.findViewById(R.id.song_artists)
        private val songImage: ImageView = itemView.findViewById(R.id.song_image)

        fun bindEvent(roomModel: Song, clickListener: OnItemClickListener) {
            itemView.setOnClickListener {
                clickListener.onItemClicked(roomModel)
            }
        }

        fun bindView(song: Song) {
            songName.text = song.songName
            songArtists.text = RoomHelper.getArtistsPlaceholder(song.artistNames)
            if (song.imageUrl != null) {
                GlideApp.with(view)
                    .load(song.imageUrl)
                    .into(songImage)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(roomModel: Song)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.row_room_queue, parent, false)
        return MViewHolder(view)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        val roomModel = songs[position]
        holder.bindView(roomModel)
        holder.bindEvent(roomModel, itemClickListener)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    fun update(data: MutableList<Song>) {
        songs = data
        notifyDataSetChanged()
    }

    fun clear() {
        songs.clear()
        notifyDataSetChanged()
    }

}
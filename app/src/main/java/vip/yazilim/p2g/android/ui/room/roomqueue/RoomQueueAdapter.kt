package vip.yazilim.p2g.android.ui.room.roomqueue

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl
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
    var songs: MutableList<Song>,
    private val itemClickListener: OnItemClickListener
) : RecyclerSwipeAdapter<RoomQueueAdapter.MViewHolder>() {

    private lateinit var view: View
    private var itemManager = SwipeItemRecyclerMangerImpl(this)

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val swipeLayout: SwipeLayout = itemView.findViewById(R.id.row_song)
        private val eventHolder: LinearLayout = itemView.findViewById(R.id.song_event_holder)

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
                    songVote.visibility = View.INVISIBLE
                }
            }

            swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
            swipeLayout.isRightSwipeEnabled = false
            swipeLayout.isClickToClose = true
            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, eventHolder)
        }

        fun bindEvent(song: Song, clickListener: OnItemClickListener) {
//            itemView.setOnClickListener { clickListener.onSongClicked(itemView, song) }
        }

        fun bindItemManager(position: Int) {
            itemManager.bindView(itemView, position)
        }
    }

    interface OnItemClickListener {
        //        fun onSongClicked(view: View, song: Song)
        fun onPlayClicked(view: SwipeLayout, song: Song)

        fun onUpvoteClicked(view: SwipeLayout, song: Song)
        fun onDownvoteClicked(view: SwipeLayout, song: Song)
        fun onDeleteClicked(view: SwipeLayout, song: Song)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.row_song, parent, false)
        return MViewHolder(view)
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.row_song
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.bindView(songs[position])
        holder.bindEvent(songs[position], itemClickListener)
        holder.bindItemManager(position)
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
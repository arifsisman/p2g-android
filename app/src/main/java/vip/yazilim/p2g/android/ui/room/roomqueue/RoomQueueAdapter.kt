package vip.yazilim.p2g.android.ui.room.roomqueue

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl
import kotlinx.android.synthetic.main.item_song.view.*
import kotlinx.android.synthetic.main.layout_row_song_events.view.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.ColorCodes.NEGATIVE_RED
import vip.yazilim.p2g.android.constant.ColorCodes.SPOTIFY_GREEN
import vip.yazilim.p2g.android.constant.ColorCodes.WHITE
import vip.yazilim.p2g.android.entity.Song
import vip.yazilim.p2g.android.util.glide.GlideApp
import vip.yazilim.p2g.android.util.helper.RoomHelper


/**
 * @author mustafaarifsisman - 20.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomQueueAdapter(
    var songs: MutableList<Song>,
    private val itemClickListener: OnItemClickListener,
    private val swipeListener: SwipeLayout.SwipeListener
) : RecyclerSwipeAdapter<RoomQueueAdapter.MViewHolder>() {

    private lateinit var view: View
    private var itemManager = SwipeItemRecyclerMangerImpl(this)

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val swipeLayout: SwipeLayout = itemView.findViewById(R.id.row_song)

        fun bindView(song: Song) {
            swipeLayout.close(false)

            itemView.song_name.text = song.songName
            itemView.song_artists.text = RoomHelper.getArtistsPlaceholder(song.artistNames, "")

            if (song.imageUrl != null) {
                GlideApp.with(view)
                    .load(song.imageUrl)
                    .into(itemView.song_image)
            } else {
                itemView.song_image.setImageResource(R.mipmap.ic_launcher)
            }

            when {
                song.votes > 0 -> {
                    val songVotePlaceholder = song.votes.toString()
                    itemView.song_vote.text = songVotePlaceholder
                    itemView.song_vote.setTextColor(Color.parseColor(SPOTIFY_GREEN))
                }
                song.votes < 0 -> {
                    val songVotePlaceholder = song.votes.toString()
                    itemView.song_vote.text = songVotePlaceholder
                    itemView.song_vote.setTextColor(Color.parseColor(NEGATIVE_RED))
                }
                else -> {
                    val songVotePlaceholder = "0"
                    itemView.song_vote.text = songVotePlaceholder
                    itemView.song_vote.setTextColor(Color.parseColor(WHITE))
                }
            }
        }

        fun bindEvent(song: Song, clickListener: OnItemClickListener) {
            itemView.swipePlayButton.setOnClickListener {
                clickListener.onPlayClicked(swipeLayout, song)
            }
            itemView.swipeUpvoteButton.setOnClickListener {
                clickListener.onUpvoteClicked(swipeLayout, song)
            }
            itemView.swipeDownvoteButton.setOnClickListener {
                clickListener.onDownvoteClicked(swipeLayout, song)
            }
            itemView.swipeDeleteButton.setOnClickListener {
                clickListener.onDeleteClicked(swipeLayout, song)
            }

            swipeLayout.addSwipeListener(swipeListener)
            swipeLayout.surfaceView.setOnClickListener { swipeLayout.open(true) }
            swipeLayout.isClickToClose = true
            swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, itemView.song_event_holder)
        }

        fun bindItemManager(position: Int) {
            itemManager.bindView(itemView, position)
        }
    }

    interface OnItemClickListener {
        fun onPlayClicked(view: SwipeLayout, song: Song)
        fun onUpvoteClicked(view: SwipeLayout, song: Song)
        fun onDownvoteClicked(view: SwipeLayout, song: Song)
        fun onDeleteClicked(view: SwipeLayout, song: Song)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return MViewHolder(view)
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.row_song
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        val song = songs[position]
        holder.bindView(song)
        holder.bindEvent(song, itemClickListener)
        holder.bindItemManager(position)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    fun update(data: MutableList<Song>) {
        songs = data
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

    fun remove(song: Song) {
        val position = songs.indexOf(song)
        val size = songs.size
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, size)

        songs.remove(song)
    }

}
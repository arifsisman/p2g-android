package vip.yazilim.p2g.android.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.haipq.android.flagkit.FlagImageView
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.model.p2g.RoomModel

/**
 * @author mustafaarifsisman - 04.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class HomeAdapter(
    var roomModels: List<RoomModel>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<HomeAdapter.MViewHolder>(), Filterable {

    private lateinit var view: View
    var roomModelsFull: List<RoomModel> = mutableListOf()

    class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomName: TextView = itemView.findViewById(R.id.room_name)
        val owner: TextView = itemView.findViewById(R.id.room_owner)
        val roomSongStatus: TextView = itemView.findViewById(R.id.room_song_status)
        val lock: ImageView = itemView.findViewById(R.id.lock_view)
        val flagImage: FlagImageView = itemView.findViewById(R.id.country_flag_image_view)

        fun bind(roomModel: RoomModel, clickListener: OnItemClickListener) {
            itemView.setOnClickListener {
                clickListener.onItemClicked(roomModel)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(roomModel: RoomModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.row_home, parent, false)
        return MViewHolder(view)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.bind(roomModels[position], itemClickListener)
        val roomModel = roomModels[position]

        val roomOwnerPlaceholder =
            view.resources.getString(R.string.placeholder_room_owner) + " " + roomModel.owner.name

        holder.roomName.text = roomModel.room.name
        holder.owner.text = roomOwnerPlaceholder

        if (roomModel.room.privateFlag) {
            holder.lock.visibility = View.VISIBLE
        } else {
            holder.lock.visibility = View.INVISIBLE
        }

        if (roomModel.songList.isNullOrEmpty()) {
            holder.roomSongStatus.text = view.resources.getString(R.string.placeholder_room_song_not_found)
        } else {
            val roomNowPlayingPlaceholder =
                view.resources.getString(R.string.placeholder_room_now_playing_song)
            val roomPausedPlaceholder = view.resources.getString(R.string.placeholder_room_paused_song)
            val roomNextSongPlaceholder = view.resources.getString(R.string.placeholder_room_next_song)


            roomModel.songList?.forEach {
                when (it.songStatus) {
                    SongStatus.PLAYING.songStatus -> {
                        val roomSongStatus =
                            roomNowPlayingPlaceholder + " " + it.songName + " - " + it.artistNames[0]
                        holder.roomSongStatus.text = roomSongStatus
                        return
                    }
                    SongStatus.PAUSED.songStatus -> {
                        val roomSongStatus =
                            roomPausedPlaceholder + " " + it.songName + " - " + it.artistNames[0]
                        holder.roomSongStatus.text = roomSongStatus
                        return
                    }
                    SongStatus.NEXT.songStatus -> {
                        val roomSongStatus =
                            roomNextSongPlaceholder + " " + it.songName + " - " + it.artistNames[0]
                        holder.roomSongStatus.text = roomSongStatus
                        return
                    }
                }
            }
        }

        try {
            holder.flagImage.countryCode = roomModel.owner.countryCode
        } catch (exception: Exception) {
            holder.flagImage.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return roomModels.size
    }

    fun update(data: List<RoomModel>) {
        roomModels = data
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                val filteredList: MutableList<RoomModel> = mutableListOf()
                val charString = constraint.toString()

                if (constraint == null || charString.isEmpty()) {
                    filteredList.addAll(roomModelsFull)
                } else {
                    val filter = constraint.toString().trim()

                    roomModelsFull.forEach {
                        if (it.room.name.contains(filter, true)
                            || it.owner.name.contains(filter, true)
                        ) {
                            filteredList.add(it)
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                update(filterResults.values as List<RoomModel>)
            }
        }
    }

}
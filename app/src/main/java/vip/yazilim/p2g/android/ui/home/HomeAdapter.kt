package vip.yazilim.p2g.android.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.model.p2g.RoomModel


class HomeAdapter(var roomModels: List<RoomModel>, private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<HomeAdapter.MViewHolder>(), Filterable {

    private lateinit var view: View
    var roomModelsFull: MutableList<RoomModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.row_home, parent, false)
        return MViewHolder(view)
    }

    override fun onBindViewHolder(vh: MViewHolder, position: Int) {
        vh.bind(roomModels[position], itemClickListener)
        val roomModel = roomModels[position]

        val roomOwnerPlaceholder = view.resources.getString(R.string.placeholder_room_owner) + roomModel.owner.name
        val roomNowPlayingPlaceholder = view.resources.getString(R.string.placeholder_room_now_playing_song)
        val roomPausedPlaceholder = view.resources.getString(R.string.placeholder_room_paused_song)
        val roomNextSongPlaceholder = view.resources.getString(R.string.placeholder_room_next_song)
        val roomSongNotFoundPlaceholder = view.resources.getString(R.string.placeholder_room_song_not_found)

        vh.roomName.text = roomModel.room.name
        vh.owner.text = roomOwnerPlaceholder

        if (roomModel.room.privateFlag) {
            vh.lock.visibility = View.VISIBLE
        }else{
            vh.lock.visibility = View.INVISIBLE
        }

        if (roomModel.songList.isNullOrEmpty()) {
            vh.roomSongStatus.text = roomSongNotFoundPlaceholder
        } else {
            roomModel.songList?.forEach {
                when (it.songStatus) {
                    SongStatus.PLAYING.songStatus -> {
                        val roomSongStatus =
                            roomNowPlayingPlaceholder + it.songName + " - " + it.artistNames[0]
                        vh.roomSongStatus.text = roomSongStatus
                        return
                    }
                    SongStatus.PAUSED.songStatus -> {
                        val roomSongStatus =
                            roomPausedPlaceholder + it.songName + " - " + it.artistNames[0]
                        vh.roomSongStatus.text = roomSongStatus
                        return
                    }
                    SongStatus.NEXT.songStatus -> {
                        val roomSongStatus =
                            roomNextSongPlaceholder + it.songName + " - " + it.artistNames[0]
                        vh.roomSongStatus.text = roomSongStatus
                        return
                    }
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
                        if (it.room.name.contains(filter,  true)
                            || it.owner.name.contains(filter,  true)
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

    class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomName: TextView = itemView.findViewById(R.id.room_name)
        val owner: TextView = itemView.findViewById(R.id.room_owner)
        val roomSongStatus: TextView = itemView.findViewById(R.id.roomSongStatus)
        val lock: ImageView = itemView.findViewById(R.id.lock_view)

        fun bind(roomModel: RoomModel, clickListener: OnItemClickListener)
        {
            itemView.setOnClickListener {
                clickListener.onItemClicked(roomModel)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClicked(roomModel: RoomModel)
    }

}
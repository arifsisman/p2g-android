package vip.yazilim.p2g.android.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.GeneralConstants.LOG_TAG
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.model.p2g.RoomModel


class HomeAdapter(private var roomModels: List<RoomModel>) :
    RecyclerView.Adapter<HomeAdapter.MViewHolder>(), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_home, parent, false)
//        view.setOnClickListener(mOnClickListener)
        return MViewHolder(view)
    }

//    fun onClick(view: View?) {
//        val itemPosition: Int = mRecyclerView.getChildLayoutPosition(view)
//        val item: String = mList.get(itemPosition)
//        Toast.makeText(mContext, item, Toast.LENGTH_LONG).show()
//    }

    override fun onBindViewHolder(vh: MViewHolder, position: Int) {
        vh.onClick(itemOnClick)

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

    private val itemOnClick: (View, Int, Int) -> Unit = { view, position, type ->
        Log.d(LOG_TAG, roomModels[position].room.name)
    }

    private fun <T : RecyclerView.ViewHolder> T.onClick(event: (view: View, position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener { event.invoke(it, adapterPosition, itemViewType) }
        return this
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

    override fun getFilter(): Filter {
        return object : Filter() {
            var filteredRoomModelList: MutableList<RoomModel> = mutableListOf()

            override fun performFiltering(charSequence: CharSequence?): FilterResults? {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    filteredRoomModelList = roomModels as MutableList<RoomModel>
                } else {
                    for (row in roomModels) {
                        if (row.room.name.contains(charString, ignoreCase = true)) {
                            filteredRoomModelList.add(row)
                        }
                    }
                    roomModels = filteredRoomModelList
                }

                val filterResults = FilterResults()
                filterResults.values = filteredRoomModelList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                update(filteredRoomModelList)
                notifyDataSetChanged()
            }
        }
    }

}
package vip.yazilim.p2g.android.ui.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_room.view.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.entity.Room
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.util.glide.GlideApp
import vip.yazilim.p2g.android.util.helper.RoomHelper

/**
 * @author mustafaarifsisman - 04.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class HomeAdapter(
    private var roomModels: MutableList<RoomModel>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<HomeAdapter.MViewHolder>(), Filterable {

    private lateinit var view: View
    var roomModelsFull: MutableList<RoomModel> = mutableListOf()

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songImage: ImageView = itemView.findViewById(R.id.song_image)

        fun bindEvent(roomModel: RoomModel, clickListener: OnItemClickListener) {
            itemView.setOnClickListener { clickListener.onItemClicked(roomModel.room) }
        }

        fun bindView(roomModel: RoomModel) {
            val roomOwnerPlaceholder =
                "${view.resources.getString(R.string.placeholder_room_owner)} ${roomModel.owner.name}"

            itemView.roomName.text = roomModel.room.name
            itemView.roomOwner.text = roomOwnerPlaceholder
            itemView.userCount.text = roomModel.userCount.toString()

            if (roomModel.room.privateFlag) {
                itemView.lockImage.visibility = View.VISIBLE
            } else {
                itemView.lockImage.visibility = View.GONE
            }

            try {
                itemView.country_flag.countryCode = roomModel.owner.countryCode
            } catch (exception: Exception) {
                itemView.country_flag.visibility = View.GONE
            }

            if (roomModel.song != null) {
                val song = roomModel.song
                if (song?.imageUrl != null) {
                    GlideApp.with(view)
                        .load(roomModel.song?.imageUrl)
                        .into(songImage)
                }

                itemView.song_name.text = song?.songName
                itemView.song_artists.text =
                    RoomHelper.getArtistsPlaceholder(roomModel.song!!.artistNames, "")
                itemView.seek_bar.max = song?.durationMs ?: 0
                itemView.seek_bar.progress = RoomViewModel.getCurrentSongMs(song)

                itemView.song_status.visibility = View.VISIBLE
            } else {
                itemView.song_status.visibility = View.GONE
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(room: Room)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false)
        return MViewHolder(view)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        val roomModel = roomModels[position]
        holder.bindView(roomModel)
        holder.bindEvent(roomModel, itemClickListener)
    }

    override fun getItemCount(): Int {
        return roomModels.size
    }

    fun update(data: MutableList<RoomModel>) {
        roomModels = data.asReversed()
        notifyDataSetChanged()
    }

    fun clear() {
        roomModels.clear()
        roomModelsFull.clear()
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
                        if (it.room.name.contains(filter, true) || (it.owner.name.contains(
                                filter,
                                true
                            ))
                        ) {
                            filteredList.add(it)
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                update(filterResults.values as MutableList<RoomModel>)
            }
        }
    }

}
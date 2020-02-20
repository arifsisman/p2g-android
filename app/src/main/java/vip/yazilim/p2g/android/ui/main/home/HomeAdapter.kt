package vip.yazilim.p2g.android.ui.main.home

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
import vip.yazilim.p2g.android.model.p2g.RoomModelSimplified
import vip.yazilim.p2g.android.util.helper.RoomHelper

/**
 * @author mustafaarifsisman - 04.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class HomeAdapter(
    private var roomModels: MutableList<RoomModelSimplified>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<HomeAdapter.MViewHolder>(), Filterable {

    private lateinit var view: View
    var roomModelsFull: MutableList<RoomModelSimplified> = mutableListOf()

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val roomName: TextView = itemView.findViewById(R.id.room_name)
        private val owner: TextView = itemView.findViewById(R.id.room_owner)
        private val roomSongStatus: TextView = itemView.findViewById(R.id.room_song_status)
        private val lock: ImageView = itemView.findViewById(R.id.lock_view)
        private val flagImage: FlagImageView = itemView.findViewById(R.id.country_flag_image_view)

        fun bindEvent(roomModel: RoomModelSimplified, clickListener: OnItemClickListener) {
            itemView.setOnClickListener {
                clickListener.onItemClicked(roomModel)
            }
        }

        fun bindView(roomModel: RoomModelSimplified){
            val roomOwnerPlaceholder =
                "${view.resources.getString(R.string.placeholder_room_owner)} ${roomModel.owner?.name}"

            roomName.text = roomModel.room?.name
            owner.text = roomOwnerPlaceholder

            if (roomModel.room?.privateFlag!!) {
                lock.visibility = View.VISIBLE
            } else {
                lock.visibility = View.GONE
            }

            try {
                flagImage.countryCode = roomModel.owner?.countryCode
            } catch (exception: Exception) {
                flagImage.visibility = View.GONE
            }

            val songStatus = RoomHelper.getRoomSongStatus(view, roomModel.song)
            roomSongStatus.text = songStatus
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(roomModel: RoomModelSimplified)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.row_home, parent, false)
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

    fun update(data: MutableList<RoomModelSimplified>) {
        roomModels = data
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
                val filteredList: MutableList<RoomModelSimplified> = mutableListOf()
                val charString = constraint.toString()

                if (constraint == null || charString.isEmpty()) {
                    filteredList.addAll(roomModelsFull)
                } else {
                    val filter = constraint.toString().trim()

                    roomModelsFull.forEach {
                        if (it.room?.name?.contains(filter, true)!!
                            || it.owner?.name?.contains(filter, true)!!
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
                update(filterResults.values as MutableList<RoomModelSimplified>)
            }
        }
    }

}
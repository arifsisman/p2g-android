package vip.yazilim.p2g.android.ui.roominvites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.model.p2g.RoomInviteModel

/**
 * @author mustafaarifsisman - 03.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomInvitesAdapter(
    var roomInviteModel: RoomInviteModel,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RoomInvitesAdapter.MViewHolder>() {

    private lateinit var view: View
    var roomInviteModelFull: RoomInviteModel = RoomInviteModel()

    class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val roomName: TextView = itemView.findViewById(R.id.room_name)
//        val owner: TextView = itemView.findViewById(R.id.room_owner)
//        val roomSongStatus: TextView = itemView.findViewById(R.id.roomSongStatus)
//        val lock: ImageView = itemView.findViewById(R.id.lock_view)
//        val flagImage: FlagImageView = itemView.findViewById(R.id.country_flag_image_view)

        fun bind(roomInviteModel: RoomInviteModel, clickListener: OnItemClickListener) {
            itemView.setOnClickListener {
                clickListener.onItemClicked(roomInviteModel)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(roomInviteModel: RoomInviteModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.row_home, parent, false)
        return MViewHolder(view)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.bind(roomInviteModel, itemClickListener)

    }

    override fun getItemCount(): Int {
        return roomInviteModel.roomInvites?.size!!
    }

    fun update(data: RoomInviteModel) {
        roomInviteModel = data
        notifyDataSetChanged()
    }

//    override fun getFilter(): Filter {
//        return object : Filter() {
//
//            override fun performFiltering(constraint: CharSequence?): FilterResults? {
//                val filteredList: MutableList<RoomModel> = mutableListOf()
//                val charString = constraint.toString()
//
//                if (constraint == null || charString.isEmpty()) {
//                    filteredList.addAll(roomModelsFull)
//                } else {
//                    val filter = constraint.toString().trim()
//
//                    roomModelsFull.forEach {
//                        if (it.room.name.contains(filter, true)
//                            || it.owner.name.contains(filter, true)
//                        ) {
//                            filteredList.add(it)
//                        }
//                    }
//                }
//
//                val results = FilterResults()
//                results.values = filteredList
//                return results
//            }
//
//            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
//                update(filterResults.values as List<RoomModel>)
//            }
//        }
//    }

}
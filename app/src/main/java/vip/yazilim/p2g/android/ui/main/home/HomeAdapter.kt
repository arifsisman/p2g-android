package vip.yazilim.p2g.android.ui.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_home.view.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.model.p2g.RoomModel
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
        fun bindEvent(roomModel: RoomModel, clickListener: OnItemClickListener) {
            itemView.setOnClickListener {
                clickListener.onItemClicked(roomModel)
            }
        }

        fun bindView(roomModel: RoomModel) {
            val roomOwnerPlaceholder =
                "${view.resources.getString(R.string.placeholder_room_owner)} ${roomModel.owner?.name}"

            itemView.roomName.text = roomModel.room?.name
            itemView.roomOwner.text = roomOwnerPlaceholder
            itemView.userCount.text = roomModel.userCount.toString()

            if (roomModel.room?.privateFlag!!) {
                itemView.lockImage.visibility = View.VISIBLE
            } else {
                itemView.lockImage.visibility = View.GONE
            }

            try {
                itemView.countryFlag.countryCode = roomModel.owner?.countryCode
            } catch (exception: Exception) {
                itemView.countryFlag.visibility = View.GONE
            }

            val songStatus = RoomHelper.getRoomSongStatus(view, roomModel.song)
            itemView.roomSongStatus.text = songStatus
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(roomModel: RoomModel)
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
                val filteredList: MutableList<RoomModel> = mutableListOf()
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
                update(filterResults.values as MutableList<RoomModel>)
            }
        }
    }

}
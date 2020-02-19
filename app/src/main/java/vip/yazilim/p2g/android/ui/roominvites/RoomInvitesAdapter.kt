package vip.yazilim.p2g.android.ui.roominvites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.OnlineStatus
import vip.yazilim.p2g.android.model.p2g.RoomInviteModel
import vip.yazilim.p2g.android.util.glide.GlideApp
import vip.yazilim.p2g.android.util.helper.RoomHelper

/**
 * @author mustafaarifsisman - 03.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomInvitesAdapter(
    private var roomInviteModels: MutableList<RoomInviteModel>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RoomInvitesAdapter.MViewHolder>(),
    Filterable {
    private lateinit var view: View
    var roomInviteModelsFull: MutableList<RoomInviteModel> = mutableListOf()

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val roomInviter: TextView = itemView.findViewById(R.id.room_inviter)
        private val roomName: TextView = itemView.findViewById(R.id.room_name)
        private val roomSongStatus: TextView = itemView.findViewById(R.id.room_song_status)
        private val profileImage: ImageView = itemView.findViewById(R.id.profile_photo_image_view)
        private val onlineStatus: ImageView = itemView.findViewById(R.id.online_status_online_image_view)
        private val acceptButton: ImageButton = itemView.findViewById(R.id.accept_button)
        private val rejectButton: ImageButton = itemView.findViewById(R.id.reject_button)

        fun bindEvent(roomInviteModel: RoomInviteModel, clickListener: OnItemClickListener) {
            itemView.setOnClickListener { clickListener.onRowClicked(roomInviteModel) }
            acceptButton.setOnClickListener { clickListener.onAccept(roomInviteModel) }
            rejectButton.setOnClickListener { clickListener.onReject(roomInviteModel) }
        }

        fun bindView(roomInviteModel: RoomInviteModel) {
            val roomModel = roomInviteModel.roomModel
            val user = roomInviteModel.inviter

            if (user?.imageUrl != null) {
                GlideApp.with(view)
                    .load(user.imageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage)
            } else {
                profileImage.setImageResource(R.drawable.ic_profile_image)
            }

            roomInviter.text = user?.name

            val roomNamePlaceholder =
                "${view.resources.getString(R.string.placeholder_room_name_expanded)} ${roomModel?.room?.name}"
            roomName.text = roomNamePlaceholder

            when (user?.onlineStatus) {
                OnlineStatus.ONLINE.onlineStatus -> {
                    onlineStatus.setImageResource(android.R.drawable.presence_online)
                    onlineStatus.visibility = View.VISIBLE
                }
                OnlineStatus.OFFLINE.onlineStatus -> {
                    onlineStatus.setImageResource(android.R.drawable.presence_offline)
                    onlineStatus.visibility = View.VISIBLE
                }
                OnlineStatus.AWAY.onlineStatus -> {
                    onlineStatus.setImageResource(android.R.drawable.presence_away)
                    onlineStatus.visibility = View.VISIBLE
                }
            }

            val songStatus = RoomHelper.getRoomSongStatus(view, roomModel?.song)
            roomSongStatus.text = songStatus
        }
    }

    interface OnItemClickListener {
        fun onAccept(roomInviteModel: RoomInviteModel)
        fun onReject(roomInviteModel: RoomInviteModel)
        fun onRowClicked(roomInviteModel: RoomInviteModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.row_room_invites, parent, false)
        return MViewHolder(view)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        val roomInviteModel = roomInviteModels[position]
        holder.bindEvent(roomInviteModel, itemClickListener)
        holder.bindView(roomInviteModel)
    }

    override fun getItemCount(): Int {
        return roomInviteModels.size
    }

    fun update(data: MutableList<RoomInviteModel>) {
        roomInviteModels = data
        notifyDataSetChanged()
    }

    fun add(data: RoomInviteModel) {
        roomInviteModels.add(data)
        notifyItemInserted(roomInviteModels.size)
    }

    fun remove(data: RoomInviteModel) {
        val position = roomInviteModels.indexOf(data)
        val size = roomInviteModels.size
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, size)

        roomInviteModels.remove(data)
        roomInviteModelsFull.remove(data)
    }

    fun removeAt(position: Int) {
        val size = roomInviteModels.size
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, size)

        roomInviteModels.removeAt(position)
        roomInviteModelsFull.removeAt(position)
    }

    fun clear() {
        roomInviteModels.clear()
        roomInviteModelsFull.clear()
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                val filteredList: MutableList<RoomInviteModel> = mutableListOf()
                val charString = constraint.toString()

                if (constraint == null || charString.isEmpty()) {
                    filteredList.addAll(roomInviteModelsFull)
                } else {
                    val filter = constraint.toString().trim()
                    roomInviteModelsFull.forEach {
                        if (it.roomModel?.room?.name?.contains(filter, true)!!
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
                update(filterResults.values as MutableList<RoomInviteModel>)
            }
        }
    }
}
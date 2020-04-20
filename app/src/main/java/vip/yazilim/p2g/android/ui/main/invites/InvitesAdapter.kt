package vip.yazilim.p2g.android.ui.main.invites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_invites.view.*
import kotlinx.android.synthetic.main.item_room.view.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.OnlineStatus
import vip.yazilim.p2g.android.model.p2g.RoomInviteModel
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.util.glide.GlideApp
import vip.yazilim.p2g.android.util.helper.RoomHelper

/**
 * @author mustafaarifsisman - 03.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class InvitesAdapter(
    private var roomInviteModels: MutableList<RoomInviteModel>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<InvitesAdapter.MViewHolder>(),
    Filterable {
    private lateinit var view: View
    var roomInviteModelsFull: MutableList<RoomInviteModel> = mutableListOf()

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindEvent(roomInviteModel: RoomInviteModel, clickListener: OnItemClickListener) {
            itemView.setOnClickListener { clickListener.onRowClicked(roomInviteModel) }
            itemView.accept_button.setOnClickListener { clickListener.onAccept(roomInviteModel) }
            itemView.reject_button.setOnClickListener { clickListener.onReject(roomInviteModel) }
        }

        fun bindView(roomInviteModel: RoomInviteModel) {
            val roomModel = roomInviteModel.userModel.roomModel
            val user = roomInviteModel.userModel.user

            if (user.imageUrl != null) {
                GlideApp.with(view)
                    .load(user.imageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(itemView.profile_photo)
            } else {
                itemView.profile_photo.setImageResource(R.drawable.ic_profile_image)
            }

            itemView.room_inviter.text = user.name

            when (user.onlineStatus) {
                OnlineStatus.ONLINE.onlineStatus -> {
                    itemView.online_status.setImageResource(android.R.drawable.presence_online)
                    itemView.online_status.visibility = View.VISIBLE
                }
                OnlineStatus.OFFLINE.onlineStatus -> {
                    itemView.online_status.setImageResource(android.R.drawable.presence_offline)
                    itemView.online_status.visibility = View.VISIBLE
                }
                OnlineStatus.AWAY.onlineStatus -> {
                    itemView.online_status.setImageResource(android.R.drawable.presence_away)
                    itemView.online_status.visibility = View.VISIBLE
                }
            }

            if (roomModel != null) {
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

                itemView.country_flag.visibility = View.GONE

                if (roomModel.song != null) {
                    val song = roomModel.song
                    if (song?.imageUrl != null) {
                        GlideApp.with(view)
                            .load(roomModel.song?.imageUrl)
                            .into(itemView.song_image)
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
    }

    interface OnItemClickListener {
        fun onAccept(roomInviteModel: RoomInviteModel)
        fun onReject(roomInviteModel: RoomInviteModel)
        fun onRowClicked(roomInviteModel: RoomInviteModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_invites, parent, false)
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
                        if (it.userModel.user.name.contains(filter, true)) {
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
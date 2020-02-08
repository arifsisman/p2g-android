package vip.yazilim.p2g.android.ui.roominvites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.OnlineStatus
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.model.p2g.RoomInviteModel
import vip.yazilim.p2g.android.model.p2g.User
import vip.yazilim.p2g.android.util.glide.GlideApp


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
        val roomInviter: TextView = itemView.findViewById(R.id.room_inviter)
        val roomName: TextView = itemView.findViewById(R.id.room_name)
        val roomSongStatus: TextView = itemView.findViewById(R.id.room_song_status)
        val profileImage: ImageView = itemView.findViewById(R.id.profile_photo_image_view)
        val onlineStatus: ImageView = itemView.findViewById(R.id.online_status_online_image_view)
        private val acceptButton: ImageButton = itemView.findViewById(R.id.accept_button)
        private val rejectButton: ImageButton = itemView.findViewById(R.id.reject_button)

        fun bindEvent(roomInviteModel: RoomInviteModel, clickListener: OnItemClickListener) {
            acceptButton.setOnClickListener { clickListener.onAcceptClicked(roomInviteModel) }
            rejectButton.setOnClickListener { clickListener.onRejectClicked(roomInviteModel) }
        }
    }

    interface OnItemClickListener {
        fun onAcceptClicked(roomInviteModel: RoomInviteModel)
        fun onRejectClicked(roomInviteModel: RoomInviteModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.row_room_invites, parent, false)
        return MViewHolder(view)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.bindEvent(roomInviteModels[position], itemClickListener)
        val roomInviteModel = roomInviteModels[position]
        val roomInvite = roomInviteModel.roomInvite
        val roomModel = roomInviteModel.roomModel

        var user = User()

        roomModel?.userList?.forEach {
            if (it.id == roomInvite?.inviterId) {
                user = it
            }
        }

        if (user.imageUrl != null) {
            GlideApp.with(view)
                .load(user.imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.profileImage)
        }

        val roomInviterPlaceholder =
            "${view.resources.getString(R.string.placeholder_room_inviter)} ${user.name}"
        holder.roomInviter.text = roomInviterPlaceholder

        val roomNamePlaceholder =
            "${view.resources.getString(R.string.placeholder_room_name_expanded)} ${roomModel?.room?.name}"
        holder.roomName.text = roomNamePlaceholder

        when (user.onlineStatus) {
            OnlineStatus.ONLINE.onlineStatus -> {
                holder.onlineStatus.setImageResource(android.R.drawable.presence_online)
                holder.onlineStatus.visibility = View.VISIBLE
            }
            OnlineStatus.OFFLINE.onlineStatus -> {
                holder.onlineStatus.setImageResource(android.R.drawable.presence_offline)
                holder.onlineStatus.visibility = View.VISIBLE
            }
            OnlineStatus.AWAY.onlineStatus -> {
                holder.onlineStatus.setImageResource(android.R.drawable.presence_away)
                holder.onlineStatus.visibility = View.VISIBLE
            }
        }

        if (roomModel?.songList.isNullOrEmpty()) {
            holder.roomSongStatus.text =
                view.resources.getString(R.string.placeholder_room_song_not_found)
        } else {
            val roomNowPlayingPlaceholder =
                view.resources.getString(R.string.placeholder_room_now_playing_song)
            val roomPausedPlaceholder =
                view.resources.getString(R.string.placeholder_room_paused_song)
            val roomNextSongPlaceholder =
                view.resources.getString(R.string.placeholder_room_next_song)

            roomModel?.songList?.forEach {
                when (it.songStatus) {
                    SongStatus.PLAYING.songStatus -> {
                        val roomSongStatus =
                            "$roomNowPlayingPlaceholder ${it.songName} - ${it.artistNames?.get(0)}"
                        holder.roomSongStatus.text = roomSongStatus
                        return
                    }
                    SongStatus.PAUSED.songStatus -> {
                        val roomSongStatus =
                            "$roomPausedPlaceholder ${it.songName} - ${it.artistNames?.get(0)}"
                        holder.roomSongStatus.text = roomSongStatus
                        return
                    }
                    SongStatus.NEXT.songStatus -> {
                        val roomSongStatus =
                            "$roomNextSongPlaceholder ${it.songName} - ${it.artistNames?.get(0)}"
                        holder.roomSongStatus.text = roomSongStatus
                        return
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return roomInviteModels.size
    }

    fun update(data: MutableList<RoomInviteModel>) {
        roomInviteModels = data
        notifyDataSetChanged()
    }

    fun remove(data: RoomInviteModel) {
        roomInviteModels.remove(data)
        roomInviteModelsFull.remove(data)
        notifyDataSetChanged()
    }

    fun clear(){
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
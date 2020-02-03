package vip.yazilim.p2g.android.ui.roominvites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.model.p2g.RoomInviteModel
import vip.yazilim.p2g.android.model.p2g.User
import vip.yazilim.p2g.android.util.glide.GlideApp

/**
 * @author mustafaarifsisman - 03.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomInvitesAdapter(
    var roomInviteModel: RoomInviteModel
) : RecyclerView.Adapter<RoomInvitesAdapter.MViewHolder>() {

    private lateinit var view: View
    var roomInviteModelFull: RoomInviteModel = RoomInviteModel()

    class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomInviter: TextView = itemView.findViewById(R.id.room_inviter)
        val roomName: TextView = itemView.findViewById(R.id.room_name)
        val roomSongStatus: TextView = itemView.findViewById(R.id.room_song_status)
        val profileImage: ImageView = itemView.findViewById(R.id.profile_photo_image_view)
        val acceptButton: ImageButton = itemView.findViewById(R.id.accept_button)
        val rejectButton: ImageButton = itemView.findViewById(R.id.reject_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.row_room_invites, parent, false)
        return MViewHolder(view)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        val roomInvite = roomInviteModel.roomInvites?.get(position)
        val roomModel = roomInviteModel.roomModels?.get(position)
        var inviter: User? = User()

        if (roomInvite != null && roomModel != null) {
            roomModel.userList.forEach {
                if (it.id == roomInvite.inviterId) {
                    inviter = it
                }
            }

            if (inviter!!.imageUrl != null) {
                GlideApp.with(view)
                    .load(inviter!!.imageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.profileImage)
            }

            val roomInviterPlaceholder =
                view.resources.getString(R.string.placeholder_room_inviter) + " " + inviter!!.name
            holder.roomInviter.text = roomInviterPlaceholder

            val roomNamePlaceholder = view.resources.getString(R.string.placeholder_room_name_expanded) + " " + roomModel.room.name
            holder.roomName.text = roomNamePlaceholder

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
        }

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
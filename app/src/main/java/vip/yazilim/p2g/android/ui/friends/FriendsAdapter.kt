package vip.yazilim.p2g.android.ui.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.model.p2g.FriendRequestModel
import vip.yazilim.p2g.android.model.p2g.UserModel

/**
 * @author mustafaarifsisman - 06.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class FriendsAdapter(
    var friendRequestModels: MutableList<FriendRequestModel>,
    var friends: MutableList<UserModel>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<FriendsAdapter.MViewHolder>(){

    private lateinit var view: View
    var friendRequestModelsFull: MutableList<FriendRequestModel> = mutableListOf()
    var friendsFull: MutableList<UserModel> = mutableListOf()

    class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val roomInviter: TextView = itemView.findViewById(R.id.room_inviter)
//        val roomName: TextView = itemView.findViewById(R.id.room_name)
//        val roomSongStatus: TextView = itemView.findViewById(R.id.room_song_status)
//        val profileImage: ImageView = itemView.findViewById(R.id.profile_photo_image_view)
        private val acceptButton: ImageButton = itemView.findViewById(R.id.accept_button)
        private val rejectButton: ImageButton = itemView.findViewById(R.id.reject_button)
        private val ignoreButton: ImageButton = itemView.findViewById(R.id.ignore_button)

        fun bind(friendRequestModel: FriendRequestModel, clickListener: OnItemClickListener) {
            acceptButton.setOnClickListener { clickListener.onAcceptClicked(friendRequestModel) }
            rejectButton.setOnClickListener { clickListener.onRejectClicked(friendRequestModel) }
            ignoreButton.setOnClickListener { clickListener.onIgnoreClicked(friendRequestModel) }
        }
    }

    interface OnItemClickListener {
        fun onAcceptClicked(friendRequestModel: FriendRequestModel)
        fun onRejectClicked(friendRequestModel: FriendRequestModel)
        fun onIgnoreClicked(friendRequestModel: FriendRequestModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.row_room_invites, parent, false)
        return MViewHolder(view)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.bind(friendRequestModels[position], itemClickListener)


    }

    override fun getItemCount(): Int {
        return friendRequestModels.size + friends.size
    }

    fun updateFriendRequestModels(data: MutableList<FriendRequestModel>) {
        friendRequestModels = data
        notifyDataSetChanged()
    }

    fun updateFriends(data: MutableList<UserModel>) {
        friends = data
        notifyDataSetChanged()
    }

    fun removeFriendRequestModel(data: FriendRequestModel) {
        friendRequestModels.remove(data)
        friendRequestModelsFull.remove(data)
        notifyDataSetChanged()
    }

    fun removeFriend(data: UserModel) {
        friends.remove(data)
        friendsFull.remove(data)
        notifyDataSetChanged()
    }

//    override fun getFilter(): Filter {
//        return object : Filter() {
//
//            override fun performFiltering(constraint: CharSequence?): FilterResults? {
//                val filteredList: MutableList<RoomInviteModel> = mutableListOf()
//                val charString = constraint.toString()
//
//                if (constraint == null || charString.isEmpty()) {
//                    filteredList.addAll(roomInviteModelsFull)
//                } else {
//                    val filter = constraint.toString().trim()
//                    roomInviteModelsFull.forEach {
//                        if (it.roomModel?.room?.name?.contains(filter, true)!!
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
//            @Suppress("UNCHECKED_CAST")
//            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
//                update(filterResults.values as MutableList<RoomInviteModel>)
//            }
//        }
//    }

}
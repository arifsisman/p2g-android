package vip.yazilim.p2g.android.ui.main.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_friend.view.*
import kotlinx.android.synthetic.main.item_room.view.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.OnlineStatus
import vip.yazilim.p2g.android.entity.Room
import vip.yazilim.p2g.android.entity.Song
import vip.yazilim.p2g.android.model.p2g.FriendRequestModel
import vip.yazilim.p2g.android.model.p2g.UserFriendModel
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.ViewHolderBase
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.util.glide.GlideApp
import vip.yazilim.p2g.android.util.helper.RoomHelper
import vip.yazilim.p2g.android.util.helper.TimeHelper.Companion.getFormattedCompact
import vip.yazilim.p2g.android.util.helper.TimeHelper.Companion.toZonedDateTime


/**
 * @author mustafaarifsisman - 06.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class FriendsAdapter(
    var adapterDataList: MutableList<Any>,
    private val requestClickListener: OnItemClickListener,
    private val friendClickListener: OnItemClickListener
) : RecyclerView.Adapter<ViewHolderBase<*>>(), Filterable {

    private lateinit var view: View

    var adapterDataListFull: MutableList<Any> = mutableListOf()

    companion object {
        private const val TYPE_REQUEST = 0
        private const val TYPE_FRIEND = 1
    }

    inner class FriendRequestViewHolder(itemView: View) :
        ViewHolderBase<FriendRequestModel>(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.rUserName)
        private val inviteDate: TextView = itemView.findViewById(R.id.rInviteDate)
        private val profilePhoto: ImageView = itemView.findViewById(R.id.rProfilePhoto)
        private val onlineStatus: ImageView =
            itemView.findViewById(R.id.rOnlineStatus)

        private val acceptButton: ImageButton = itemView.findViewById(R.id.rAcceptButton)
        private val rejectButton: ImageButton = itemView.findViewById(R.id.rRejectButton)

        private fun bindEvent(
            friendRequestModel: FriendRequestModel,
            clickListener: OnItemClickListener
        ) {
            itemView.setOnClickListener { clickListener.onRowClicked(friendRequestModel.userModel) }
            acceptButton.setOnClickListener { clickListener.onAcceptClicked(friendRequestModel) }
            rejectButton.setOnClickListener { clickListener.onRejectClicked(friendRequestModel) }
        }

        override fun bindView(item: FriendRequestModel) {
            bindEvent(item, requestClickListener)
            val user = item.userModel.user

            val inviteDatePlaceholder =
                "${view.resources.getString(R.string.placeholder_friend_request_date)} ${item.friendRequest.requestDate.toZonedDateTime()
                    .getFormattedCompact()}"

            userName.text = user.name
            inviteDate.text = inviteDatePlaceholder

            if (user.imageUrl != null) {
                GlideApp.with(view)
                    .load(user.imageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePhoto)
            } else {
                profilePhoto.setImageResource(R.drawable.ic_profile_image)
            }

            when (user.onlineStatus) {
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
        }
    }

    inner class FriendViewHolder(itemView: View) : ViewHolderBase<UserModel>(itemView) {
        private val songImage: ImageView = itemView.findViewById(R.id.song_image)

        private fun bindEvent(userModel: UserModel, clickListener: OnItemClickListener) {
            itemView.user.setOnClickListener { clickListener.onRowClicked(userModel) }
            itemView.deleteButton.setOnClickListener { clickListener.onDeleteClicked(userModel) }
            itemView.joinButton.setOnClickListener {
                userModel.roomModel?.room?.let { room -> clickListener.onJoinClicked(room) }
            }
        }

        override fun bindView(item: UserModel) {
            bindEvent(item, friendClickListener)
            val user = item.user
            val roomModel = item.roomModel

            itemView.user.userName.text = user.name

            if (user.imageUrl != null) {
                GlideApp.with(view)
                    .load(user.imageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(itemView.user.profilePhoto)
            } else {
                itemView.user.profilePhoto.setImageResource(R.drawable.ic_profile_image)
            }

            when (user.onlineStatus) {
                OnlineStatus.ONLINE.onlineStatus -> {
                    itemView.user.onlineStatus.setImageResource(android.R.drawable.presence_online)
                    itemView.user.onlineStatus.visibility = View.VISIBLE
                }
                OnlineStatus.OFFLINE.onlineStatus -> {
                    itemView.user.onlineStatus.setImageResource(android.R.drawable.presence_offline)
                    itemView.user.onlineStatus.visibility = View.VISIBLE
                }
                OnlineStatus.AWAY.onlineStatus -> {
                    itemView.user.onlineStatus.setImageResource(android.R.drawable.presence_away)
                    itemView.user.onlineStatus.visibility = View.VISIBLE
                }
            }

            if (roomModel != null) {
                val song: Song? = roomModel.song

                val roomOwnerPlaceholder =
                    "${view.resources.getString(R.string.placeholder_room_owner)} ${item.roomModel?.owner?.name}"

                itemView.roomName.text = roomModel.room.name
                itemView.roomOwner.text = roomOwnerPlaceholder
                itemView.userCount.text = roomModel.userCount.toString()

                if (roomModel.room.privateFlag) {
                    itemView.lockImage.visibility = View.VISIBLE
                } else {
                    itemView.lockImage.visibility = View.GONE
                }

                itemView.countryFlag.visibility = View.GONE

                if (song != null) {
                    if (song.imageUrl != null) {
                        GlideApp.with(view)
                            .load(song.imageUrl)
                            .into(songImage)
                    }

                    itemView.song_name.text = song.songName
                    itemView.song_artists.text =
                        RoomHelper.getArtistsPlaceholder(song.artistNames, "")
                    itemView.seek_bar.max = song.durationMs
                    itemView.seek_bar.progress = RoomViewModel.getCurrentSongMs(song)

                    itemView.song_status.visibility = View.VISIBLE
                } else {
                    itemView.song_status.visibility = View.GONE
                }
            } else {
                itemView.joinButton.visibility = View.GONE
                itemView.room.visibility = View.GONE
                itemView.divider.visibility = View.GONE
            }
        }
    }

    interface OnItemClickListener {
        fun onAcceptClicked(friendRequestModel: FriendRequestModel)
        fun onRejectClicked(friendRequestModel: FriendRequestModel)
        fun onIgnoreClicked(friendRequestModel: FriendRequestModel)
        fun onJoinClicked(room: Room)
        fun onDeleteClicked(userModel: UserModel)
        fun onRowClicked(userModel: UserModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<*> {
        return when (viewType) {
            TYPE_REQUEST -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_friend_request, parent, false)
                FriendRequestViewHolder(view)
            }
            TYPE_FRIEND -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
                FriendViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolderBase<*>, position: Int) {
        val element = adapterDataList[position]
        when (holder) {
            is FriendRequestViewHolder -> holder.bindView(element as FriendRequestModel)
            is FriendViewHolder -> holder.bindView(element as UserModel)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (adapterDataList[position]) {
            is FriendRequestModel -> TYPE_REQUEST
            is UserModel -> TYPE_FRIEND
            else -> throw IllegalArgumentException("Invalid type of data $position")
        }
    }

    override fun getItemCount(): Int {
        return adapterDataList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                val filteredList: MutableList<Any> = mutableListOf()
                val charString = constraint.toString()

                if (constraint == null || charString.isEmpty()) {
                    filteredList.addAll(adapterDataListFull)
                } else {
                    val filter = constraint.toString().trim()
                    adapterDataListFull.forEach {
                        when (it) {
                            is FriendRequestModel -> {
                                if (it.userModel.user.name.contains(filter, true)
                                ) {
                                    filteredList.add(it)
                                }
                            }
                            is UserModel -> {
                                if (it.user.name.contains(filter, true)
                                ) {
                                    filteredList.add(it)
                                }
                            }
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                clearDataList()
                addAll(filterResults.values as MutableList<Any>)
            }
        }
    }

    fun add(data: Any) {
        adapterDataList.add(data)
        adapterDataList.sortBy { it is UserModel }
        if (data is UserModel) {
            adapterDataList.sortBy { data.user.onlineStatus }
        }
        notifyItemInserted(adapterDataList.size)
    }

    fun addAll(data: MutableList<Any>) {
        adapterDataList.addAll(data)
        adapterDataList.sortBy { it is UserModel }
        notifyDataSetChanged()
    }

    fun update(data: UserFriendModel) {
        adapterDataList.addAll(data.requestModels)
        adapterDataList.addAll(data.friendModels)
        notifyDataSetChanged()
    }

    fun clearDataList() {
        adapterDataList.clear()
        notifyDataSetChanged()
    }

    fun clearDataListFull() {
        adapterDataListFull.clear()
        notifyDataSetChanged()
    }

    fun remove(data: Any) {
        val position = adapterDataList.indexOf(data)
        val size = adapterDataList.size
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, size)

        adapterDataList.remove(data)
        adapterDataListFull.remove(data)
    }

}
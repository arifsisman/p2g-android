package vip.yazilim.p2g.android.ui.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.OnlineStatus
import vip.yazilim.p2g.android.model.p2g.FriendRequestModel
import vip.yazilim.p2g.android.model.p2g.Room
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.ViewHolderBase
import vip.yazilim.p2g.android.util.glide.GlideApp
import vip.yazilim.p2g.android.util.helper.TimeHelper.Companion.dateTimeFormatterCompact


/**
 * @author mustafaarifsisman - 06.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class FriendsAdapter(
    private var adapterDataList: MutableList<Any>,
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
        private val userName: TextView = itemView.findViewById(R.id.user_name)
        private val inviteDate: TextView = itemView.findViewById(R.id.invite_date)
        private val profileImage: ImageView = itemView.findViewById(R.id.profile_photo_image_view)
        private val onlineStatus: ImageView =
            itemView.findViewById(R.id.online_status_online_image_view)

        private val acceptButton: ImageButton = itemView.findViewById(R.id.accept_button)
        private val rejectButton: ImageButton = itemView.findViewById(R.id.reject_button)

        private fun bindEvent(
            friendRequestModel: FriendRequestModel,
            clickListener: OnItemClickListener
        ) {
            acceptButton.setOnClickListener { clickListener.onAcceptClicked(friendRequestModel) }
            rejectButton.setOnClickListener { clickListener.onRejectClicked(friendRequestModel) }
        }

        override fun bindView(item: FriendRequestModel) {
            bindEvent(item, requestClickListener)
            val user = item.friendRequestUserModel?.user

            val inviteDatePlaceholder =
                "${view.resources.getString(R.string.placeholder_friend_request_date)} ${item.friendRequest?.requestDate?.format(
                    dateTimeFormatterCompact
                )}"

            userName.text = user?.name
            inviteDate.text = inviteDatePlaceholder

            if (user?.imageUrl != null) {
                GlideApp.with(view)
                    .load(user.imageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage)
            }else{
                profileImage.setImageResource(R.drawable.ic_profile_image)
            }

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
        }
    }

    inner class FriendViewHolder(itemView: View) : ViewHolderBase<UserModel>(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.user_name)
        private val roomName: TextView = itemView.findViewById(R.id.room_name)
        private val profileImage: ImageView = itemView.findViewById(R.id.profile_photo_image_view)
        private val onlineStatus: ImageView =
            itemView.findViewById(R.id.online_status_online_image_view)
        private val lock: ImageView = itemView.findViewById(R.id.lock_view)

        private val joinButton: ImageButton = itemView.findViewById(R.id.join_button)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        private fun bindEvent(userModel: UserModel, clickListener: OnItemClickListener) {
            deleteButton.setOnClickListener { clickListener.onDeleteClicked(userModel) }
            joinButton.setOnClickListener { clickListener.onJoinClicked(userModel.room) }

        }

        override fun bindView(item: UserModel) {
            bindEvent(item, friendClickListener)
            val user = item.user
            val room = item.room



            userName.text = user?.name

            if (user?.imageUrl != null) {
                GlideApp.with(view)
                    .load(user.imageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage)
            }else{
                profileImage.setImageResource(R.drawable.ic_profile_image)
            }

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

            if (room != null) {
                val roomNamePlaceholder =
                    "${view.resources.getString(R.string.placeholder_room_name_expanded)} ${room.name}"
                roomName.text = roomNamePlaceholder
                if (room.privateFlag) {
                    lock.visibility = View.VISIBLE
                    joinButton.visibility = View.INVISIBLE
                } else {
                    lock.visibility = View.INVISIBLE
                    joinButton.visibility = View.VISIBLE
                }
            } else {
                roomName.visibility = View.INVISIBLE
                lock.visibility = View.INVISIBLE
                joinButton.visibility = View.INVISIBLE
            }

        }
    }

    interface OnItemClickListener {
        fun onAcceptClicked(friendRequestModel: FriendRequestModel)
        fun onRejectClicked(friendRequestModel: FriendRequestModel)
        fun onIgnoreClicked(friendRequestModel: FriendRequestModel)
        fun onJoinClicked(room: Room?)
        fun onDeleteClicked(userModel: UserModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<*> {
        return when (viewType) {
            TYPE_REQUEST -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_friend_request, parent, false)
                FriendRequestViewHolder(view)
            }
            TYPE_FRIEND -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.row_friend, parent, false)
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
                                if (it.friendRequestUserModel?.user?.name?.contains(filter, true)!!
                                ) {
                                    filteredList.add(it)
                                }
                            }
                            is UserModel -> {
                                if (it.user?.name?.contains(filter, true)!!
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
        notifyDataSetChanged()
    }

    fun addAll(data: MutableList<Any>) {
        adapterDataList.addAll(data)
        adapterDataList.sortBy { it is UserModel }
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
        adapterDataList.remove(data)
        adapterDataListFull.remove(data)
        notifyDataSetChanged()
    }

}
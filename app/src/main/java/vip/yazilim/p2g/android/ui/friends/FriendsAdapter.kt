package vip.yazilim.p2g.android.ui.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.model.p2g.FriendRequestModel
import vip.yazilim.p2g.android.model.p2g.Room
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.util.glide.GlideApp


/**
 * @author mustafaarifsisman - 06.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class FriendsAdapter(
    private var adapterDataList: MutableList<*>,
    private val requestClickListener: OnItemClickListener,
    private val friendClickListener: OnItemClickListener
) : RecyclerView.Adapter<FriendsAdapter.BaseViewHolder<*>>() {

    private lateinit var view: View

    var adapterDataListFull: MutableList<Any> = mutableListOf()

    companion object {
        private const val TYPE_REQUEST = 0
        private const val TYPE_FRIEND = 1
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bindView(item: T)
    }

    inner class FriendRequestViewHolder(itemView: View) :
        BaseViewHolder<FriendRequestModel>(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.user_name)
        private val inviteDate: TextView = itemView.findViewById(R.id.invite_date)
        private val profileImage: ImageView = itemView.findViewById(R.id.profile_photo_image_view)

        private val acceptButton: ImageButton = itemView.findViewById(R.id.accept_button)
        private val rejectButton: ImageButton = itemView.findViewById(R.id.reject_button)
        private val ignoreButton: ImageButton = itemView.findViewById(R.id.ignore_button)

        private fun bindEvent(
            friendRequestModel: FriendRequestModel,
            clickListener: OnItemClickListener
        ) {
            acceptButton.setOnClickListener { clickListener.onAcceptClicked(friendRequestModel) }
            rejectButton.setOnClickListener { clickListener.onRejectClicked(friendRequestModel) }
            ignoreButton.setOnClickListener { clickListener.onIgnoreClicked(friendRequestModel) }
        }

        override fun bindView(item: FriendRequestModel) {
            bindEvent(item, requestClickListener)
            val user = item.friendRequestUser

            val userNamePlaceholder =
                "${view.resources.getString(R.string.placeholder_user_name)} ${user?.name}"
            val inviteDatePlaceholder =
                "${view.resources.getString(R.string.placeholder_friend_request_date)} ${item.friendRequest?.requestDate}"

            userName.text = userNamePlaceholder
            inviteDate.text = inviteDatePlaceholder

            GlideApp.with(view)
                .load(user?.imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(profileImage)
        }
    }

    inner class FriendViewHolder(itemView: View) : BaseViewHolder<UserModel>(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.user_name)
        private val roomName: TextView = itemView.findViewById(R.id.room_name)
        private val profileImage: ImageView = itemView.findViewById(R.id.profile_photo_image_view)

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

            val roomNamePlaceholder =
                "${view.resources.getString(R.string.placeholder_room_name_expanded)} ${room?.name}"

            userName.text = user?.name
            roomName.text = roomNamePlaceholder

            GlideApp.with(view)
                .load(user?.imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(profileImage)
        }
    }

    interface OnItemClickListener {
        fun onAcceptClicked(friendRequestModel: FriendRequestModel)
        fun onRejectClicked(friendRequestModel: FriendRequestModel)
        fun onIgnoreClicked(friendRequestModel: FriendRequestModel)
        fun onJoinClicked(room: Room?)
        fun onDeleteClicked(userModel: UserModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
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

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
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

    fun update(data: MutableList<Any>) {
        adapterDataList = data
        notifyDataSetChanged()
    }

    fun remove(data: Any) {
        adapterDataList.remove(data)
        adapterDataListFull.remove(data)
        notifyDataSetChanged()
    }

}
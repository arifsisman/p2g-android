package vip.yazilim.p2g.android.ui.friends

import android.view.View
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
) : RecyclerView.Adapter<FriendsAdapter.BaseViewHolder<*>>(){

    private lateinit var view: View

    private var adapterDataList: List<Any> = emptyList()

    var friendRequestModelsFull: MutableList<FriendRequestModel> = mutableListOf()
    var friendsFull: MutableList<UserModel> = mutableListOf()

    companion object {
        private const val TYPE_REQUEST = 0
        private const val TYPE_FRIEND = 1
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bindView(item: T)
    }

    inner class FriendRequestViewHolder(itemView: View) : BaseViewHolder<FriendRequestModel>(itemView) {
        private val acceptButton: ImageButton = itemView.findViewById(R.id.accept_button)
        private val rejectButton: ImageButton = itemView.findViewById(R.id.reject_button)
        private val ignoreButton: ImageButton = itemView.findViewById(R.id.ignore_button)

        fun bindEvent(friendRequestModel: FriendRequestModel, clickListener: OnItemClickListener) {
            acceptButton.setOnClickListener { clickListener.onAcceptClicked(friendRequestModel) }
            rejectButton.setOnClickListener { clickListener.onRejectClicked(friendRequestModel) }
            ignoreButton.setOnClickListener { clickListener.onIgnoreClicked(friendRequestModel) }
        }

        override fun bindView(item: FriendRequestModel) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    class FriendViewHolder(itemView: View) : BaseViewHolder<UserModel>(itemView) {
        fun bindEvent(friendRequestModel: FriendRequestModel, clickListener: OnItemClickListener) {

        }

        override fun bindView(item: UserModel) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    interface OnItemClickListener {
        fun onAcceptClicked(friendRequestModel: FriendRequestModel)
        fun onRejectClicked(friendRequestModel: FriendRequestModel)
        fun onIgnoreClicked(friendRequestModel: FriendRequestModel)
    }

    override fun getItemViewType(position: Int): Int {
        return position % 2 * 2
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
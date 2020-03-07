package vip.yazilim.p2g.android.ui.room.roomusers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl
import kotlinx.android.synthetic.main.layout_row_user_events.view.*
import kotlinx.android.synthetic.main.row_user_model.view.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.OnlineStatus
import vip.yazilim.p2g.android.model.p2g.RoomUserModel
import vip.yazilim.p2g.android.util.glide.GlideApp

/**
 * @author mustafaarifsisman - 07.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomUsersAdapter(
    var roomUserModelList: MutableList<RoomUserModel>,
    private val itemClickListener: OnItemClickListener
) : RecyclerSwipeAdapter<RoomUsersAdapter.MViewHolder>() {

    private lateinit var view: View
    private var itemManager = SwipeItemRecyclerMangerImpl(this)

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val swipeLayout: SwipeLayout = itemView.findViewById(R.id.row_user_model)
        private val eventHolder: LinearLayout = itemView.findViewById(R.id.user_event_holder)

        fun bindView(roomUserModel: RoomUserModel) {
            val user = roomUserModel.user
            val roomUser = roomUserModel.roomUser

            if (user != null && roomUser != null) {
                itemView.user_name.text = user.name
                itemView.user_role.text = roomUser.role

                if (user.imageUrl != null) {
                    GlideApp.with(view)
                        .load(user.imageUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(itemView.user_image)
                } else {
                    itemView.user_image.setImageResource(R.drawable.ic_profile_image)
                }

                when (user.onlineStatus) {
                    OnlineStatus.ONLINE.onlineStatus -> {
                        itemView.onlineStatus.setImageResource(android.R.drawable.presence_online)
                        itemView.onlineStatus.visibility = View.VISIBLE
                    }
                    OnlineStatus.OFFLINE.onlineStatus -> {
                        itemView.onlineStatus.setImageResource(android.R.drawable.presence_offline)
                        itemView.onlineStatus.visibility = View.VISIBLE
                    }
                    OnlineStatus.AWAY.onlineStatus -> {
                        itemView.onlineStatus.setImageResource(android.R.drawable.presence_away)
                        itemView.onlineStatus.visibility = View.VISIBLE
                    }
                }
            }

            swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
            swipeLayout.isClickToClose = true
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, eventHolder)
        }

        fun bindEvent(roomUserModel: RoomUserModel, clickListener: OnItemClickListener) {
            itemView.setOnClickListener { clickListener.onItemClicked(swipeLayout, roomUserModel) }
            itemView.swipePromoteButton.setOnClickListener {
                clickListener.onPromoteClicked(
                    swipeLayout,
                    roomUserModel
                )
            }
            itemView.swipeDemoteButton.setOnClickListener {
                clickListener.onDemoteClicked(
                    swipeLayout,
                    roomUserModel
                )
            }
            itemView.swipeAddButton.setOnClickListener {
                clickListener.onAddClicked(
                    swipeLayout,
                    roomUserModel
                )
            }
        }

        fun bindItemManager(position: Int) {
            itemManager.bindView(itemView, position)
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(view: SwipeLayout, roomUserModel: RoomUserModel)
        fun onPromoteClicked(view: SwipeLayout, roomUserModel: RoomUserModel)
        fun onDemoteClicked(view: SwipeLayout, roomUserModel: RoomUserModel)
        fun onAddClicked(view: SwipeLayout, roomUserModel: RoomUserModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.row_user_model, parent, false)
        return MViewHolder(view)
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.row_user_model
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.bindView(roomUserModelList[position])
        holder.bindEvent(roomUserModelList[position], itemClickListener)
        holder.bindItemManager(position)
    }

    override fun getItemCount(): Int {
        return roomUserModelList.size
    }

    fun update(data: MutableList<RoomUserModel>) {
        //todo sort by role
        roomUserModelList = data
        notifyDataSetChanged()
    }

}
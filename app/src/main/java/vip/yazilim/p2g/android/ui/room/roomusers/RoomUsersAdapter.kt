package vip.yazilim.p2g.android.ui.room.roomusers

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl
import kotlinx.android.synthetic.main.item_room_user_model.view.*
import kotlinx.android.synthetic.main.layout_row_user_events.view.*
import vip.yazilim.p2g.android.Play2GetherApplication
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.ColorCodes.CYAN
import vip.yazilim.p2g.android.constant.ColorCodes.GREEN
import vip.yazilim.p2g.android.constant.ColorCodes.RED
import vip.yazilim.p2g.android.constant.ColorCodes.WHITE
import vip.yazilim.p2g.android.constant.enums.Role
import vip.yazilim.p2g.android.model.p2g.RoomUserModel
import vip.yazilim.p2g.android.util.glide.GlideApp


/**
 * @author mustafaarifsisman - 07.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomUsersAdapter(
    private var roomUserModelList: MutableList<RoomUserModel>,
    private val itemClickListener: OnItemClickListener,
    private val swipeListener: SwipeLayout.SwipeListener
) : RecyclerSwipeAdapter<RoomUsersAdapter.MViewHolder>() {

    private lateinit var view: View
    private var itemManager = SwipeItemRecyclerMangerImpl(this)
    private var userIdMe: String? = "-"

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val swipeLayout: SwipeLayout = itemView.findViewById(R.id.row_user_model)
        fun bindView(roomUserModel: RoomUserModel) {
            swipeLayout.close(false)

            val user = roomUserModel.user
            val roomUser = roomUserModel.roomUser

            itemView.user_name.text = user.name
            itemView.user_role.text = roomUser.role

            when (roomUser.role) {
                Role.ROOM_OWNER.role -> {
                    itemView.user_role.setTextColor(Color.parseColor(RED))
                }
                Role.ROOM_ADMIN.role -> {
                    itemView.user_role.setTextColor(Color.parseColor(CYAN))
                }
                Role.ROOM_DJ.role -> {
                    itemView.user_role.setTextColor(Color.parseColor(GREEN))
                }
                Role.ROOM_USER.role -> {
                    itemView.user_role.setTextColor(Color.parseColor(WHITE))
                }
            }

            if (user.imageUrl != null) {
                GlideApp.with(view)
                    .load(user.imageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(itemView.user_image)
            } else {
                itemView.user_image.setImageResource(R.drawable.ic_profile_image)
            }
        }

        fun bindEvent(roomUserModel: RoomUserModel, clickListener: OnItemClickListener) {
            itemView.swipeChangeRoleButton.setOnClickListener {
                clickListener.onChangeRoleClicked(swipeLayout, roomUserModel)
            }
            itemView.swipeAddButton.setOnClickListener {
                clickListener.onAddClicked(swipeLayout, roomUserModel)
            }
            swipeLayout.addSwipeListener(swipeListener)
            swipeLayout.surfaceView.setOnClickListener { swipeLayout.open(true) }
            swipeLayout.isClickToClose = true
            swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, itemView.user_event_holder)
        }

        fun bindItemManager(position: Int) {
            itemManager.bindView(itemView, position)
        }
    }

    interface OnItemClickListener {
        fun onChangeRoleClicked(view: SwipeLayout, roomUserModel: RoomUserModel)
        fun onAddClicked(view: SwipeLayout, roomUserModel: RoomUserModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_room_user_model, parent, false)
        return MViewHolder(view)
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.row_user_model
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        val roomUserModel = roomUserModelList[position]
        holder.bindView(roomUserModel)

        if (roomUserModel.user.id == userIdMe) {
            holder.swipeLayout.isSwipeEnabled = false
        } else {
            holder.bindEvent(roomUserModelList[position], itemClickListener)
            holder.bindItemManager(position)
        }
    }

    override fun getItemCount(): Int {
        return roomUserModelList.size
    }

    fun update(data: MutableList<RoomUserModel>) {
        userIdMe = Play2GetherApplication.user?.id
        roomUserModelList = data
        notifyDataSetChanged()
    }

}
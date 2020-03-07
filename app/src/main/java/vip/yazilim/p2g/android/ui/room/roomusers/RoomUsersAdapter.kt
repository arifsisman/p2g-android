package vip.yazilim.p2g.android.ui.room.roomusers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.model.p2g.RoomUserModel

/**
 * @author mustafaarifsisman - 07.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomUsersAdapter(
    var roomUserModels: MutableList<RoomUserModel>,
    private val itemClickListener: OnItemClickListener
) : RecyclerSwipeAdapter<RoomUsersAdapter.MViewHolder>() {

    private lateinit var view: View
    private var itemManager = SwipeItemRecyclerMangerImpl(this)

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val swipeLayout: SwipeLayout = itemView.findViewById(R.id.row_user_model)

        fun bindView(roomUserModel: RoomUserModel) {
//            swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
//            swipeLayout.isClickToClose = true
//            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, eventHolder)
        }

        fun bindEvent(roomUserModel: RoomUserModel, clickListener: OnItemClickListener) {
            itemView.setOnClickListener { clickListener.onItemClicked(swipeLayout, roomUserModel) }
        }

        fun bindItemManager(position: Int) {
            itemManager.bindView(itemView, position)
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(view: SwipeLayout, roomUserModel: RoomUserModel)
        fun onPromoteClicked(view: SwipeLayout, roomUserModel: RoomUserModel)
        fun onDemoteClicked(view: SwipeLayout, roomUserModel: RoomUserModel)
        fun onAddAsFriendClicked(view: SwipeLayout, roomUserModel: RoomUserModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.row_user_model, parent, false)
        return MViewHolder(view)
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.row_song
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.bindView(roomUserModels[position])
        holder.bindEvent(roomUserModels[position], itemClickListener)
        holder.bindItemManager(position)
    }

    override fun getItemCount(): Int {
        return roomUserModels.size
    }

    fun update(data: MutableList<RoomUserModel>) {
        roomUserModels = data
        notifyDataSetChanged()
    }

}
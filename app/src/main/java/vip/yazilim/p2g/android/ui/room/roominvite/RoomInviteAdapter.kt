package vip.yazilim.p2g.android.ui.room.roominvite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.daimajia.swipe.SwipeLayout
import kotlinx.android.synthetic.main.item_room_invite.view.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.entity.User
import vip.yazilim.p2g.android.util.glide.GlideApp

/**
 * @author mustafaarifsisman - 14.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomInviteAdapter(
    private var userList: MutableList<User>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RoomInviteAdapter.MViewHolder>() {

    private lateinit var view: View

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(user: User) {
            itemView.user_name.text = user.name

            if (user.imageUrl != null) {
                GlideApp.with(view)
                    .load(user.imageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(itemView.user_image)
            } else {
                itemView.user_image.setImageResource(R.drawable.ic_profile_image)
            }
        }

        fun bindEvent(user: User, clickListener: OnItemClickListener) {
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(view: SwipeLayout, user: User)
        fun onInviteClicked(view: SwipeLayout, user: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_room_invite, parent, false)
        return MViewHolder(view)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.bindView(userList[position])
        holder.bindEvent(userList[position], itemClickListener)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun update(data: MutableList<User>) {
        userList = data
        notifyDataSetChanged()
    }

}
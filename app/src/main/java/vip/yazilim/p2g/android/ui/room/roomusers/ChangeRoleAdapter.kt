package vip.yazilim.p2g.android.ui.room.roomusers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_change_role.view.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.Role
import vip.yazilim.p2g.android.model.p2g.RoomUserModel

/**
 * @author mustafaarifsisman - 26.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class ChangeRoleAdapter(
    private var roomUserModel: RoomUserModel,
    private var roleList: List<Role>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ChangeRoleAdapter.MViewHolder>() {

    private lateinit var view: View

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(role: Role) {
            itemView.role_name.text = role.role
        }

        fun bindEvent(role: Role, clickListener: OnItemClickListener) {
            itemView.setOnClickListener {
                clickListener.onItemClicked(
                    itemView,
                    roomUserModel,
                    role
                )
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(view: View, roomUserModel: RoomUserModel, role: Role)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_change_role, parent, false)
        return MViewHolder(view)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.bindView(roleList[position])
        holder.bindEvent(roleList[position], itemClickListener)
    }

    override fun getItemCount(): Int {
        return roleList.size
    }

    fun update(data: List<Role>) {
        roleList = data
        notifyDataSetChanged()
    }
}
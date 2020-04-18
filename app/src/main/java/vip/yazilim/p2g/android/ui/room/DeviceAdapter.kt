package vip.yazilim.p2g.android.ui.room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_change_device.view.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.entity.UserDevice

/**
 * @author mustafaarifsisman - 29.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class DeviceAdapter(
    private var devices: MutableList<UserDevice>,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<DeviceAdapter.MViewHolder>() {
    private lateinit var view: View

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindEvent(searchModel: UserDevice, clickListener: OnItemClickListener) {
            itemView.setOnClickListener { clickListener.onDeviceClicked(searchModel) }
        }

        fun bindView(userDevice: UserDevice) {
            itemView.device_name.text = userDevice.deviceName
            itemView.device_type.text = userDevice.deviceType

            if (userDevice.activeFlag) {
                itemView.divider.visibility = View.VISIBLE
            } else {
                itemView.divider.visibility = View.INVISIBLE
            }
        }
    }

    interface OnItemClickListener {
        fun onDeviceClicked(userDevice: UserDevice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_change_device, parent, false)
        return MViewHolder(view)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        val userDevice = devices[position]
        holder.bindView(userDevice)
        holder.bindEvent(userDevice, itemClickListener)
    }

    fun update(data: MutableList<UserDevice>) {
        devices = data
        notifyDataSetChanged()
    }

}
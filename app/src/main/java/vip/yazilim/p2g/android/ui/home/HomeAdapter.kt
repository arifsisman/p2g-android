package vip.yazilim.p2g.android.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.model.p2g.RoomModel

class HomeAdapter(private var roomModels: List<RoomModel>) : RecyclerView.Adapter<HomeAdapter.MViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_home, parent, false)
        return MViewHolder(view)
    }

    override fun onBindViewHolder(vh: MViewHolder, position: Int) {
        val roomModel = roomModels[position]

        //render
        vh.textViewName.text = roomModel.room.name
//        Glide.with(vh.imageView.context).load(room.photo).into(vh.imageView)
    }

    override fun getItemCount(): Int {
        return roomModels.size
    }

    fun update(data: List<RoomModel>) {
        roomModels = data
        notifyDataSetChanged()
    }

    class MViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.textViewName)
//        val imageView: ImageView = view.findViewById(R.id.imageView)
//        val textViewLink: TextView = view.findViewById(R.id.textViewLink)
    }
}
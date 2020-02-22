package vip.yazilim.p2g.android.ui.room.roomqueue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.model.p2g.SearchModel
import vip.yazilim.p2g.android.util.glide.GlideApp
import vip.yazilim.p2g.android.util.helper.RoomHelper

/**
 * @author mustafaarifsisman - 22.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class SearchAdapter(var searchModels: MutableList<SearchModel>) :
    RecyclerView.Adapter<SearchAdapter.MViewHolder>() {
    private lateinit var view: View

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val searchModelName: TextView = itemView.findViewById(R.id.search_model_name)
        private val searchModelArtists: TextView = itemView.findViewById(R.id.search_model_artists)
        private val searchModelType: TextView = itemView.findViewById(R.id.search_model_type)
        private val searchModelImage: ImageView = itemView.findViewById(R.id.search_model_image)

        fun bindView(searchModel: SearchModel) {
            searchModelName.text = searchModel.name
            searchModelType.text = searchModel.type

            if (!searchModel.artistNames.isNullOrEmpty()) {
                searchModelArtists.text = RoomHelper.getArtistsPlaceholder(searchModel.artistNames)
            } else {
                searchModelArtists.visibility = View.GONE
            }

            if (searchModel.imageUrl != null) {
                GlideApp.with(view)
                    .load(searchModel.imageUrl)
                    .into(searchModelImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.row_search, parent, false)
        return MViewHolder(view)
    }

    override fun getItemCount(): Int {
        return searchModels.size
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        val roomModel = searchModels[position]
        holder.bindView(roomModel)
    }

    fun update(data: MutableList<SearchModel>) {
        searchModels = data
        notifyDataSetChanged()
    }
}
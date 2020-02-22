package vip.yazilim.p2g.android.ui.room.roomqueue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
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
class SearchAdapter(
    private var searchModels: MutableList<SearchModel>,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<SearchAdapter.MViewHolder>() {
    private lateinit var view: View

    private var selectedSearchModels = mutableListOf<SearchModel>()

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val searchModelName: TextView = itemView.findViewById(R.id.search_model_name)
        private val searchModelTypeAndArtists: TextView =
            itemView.findViewById(R.id.search_model_type_and_artists)
        private val searchModelImage: ImageView = itemView.findViewById(R.id.search_model_image)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        fun bindEvent(searchModel: SearchModel, clickListener: OnItemClickListener) {
            itemView.setOnClickListener {
                clickListener.onSearchItemClicked(searchModel)
            }
        }

        fun bindView(searchModel: SearchModel) {
            searchModelName.text = searchModel.name

            val typeAndArtistsPlaceholder =
                searchModel.type.toString() + RoomHelper.getArtistsPlaceholder(
                    searchModel.artistNames,
                    " • "
                )
            searchModelTypeAndArtists.text = typeAndArtistsPlaceholder

            if (searchModel.imageUrl != null) {
                GlideApp.with(view)
                    .load(searchModel.imageUrl)
                    .into(searchModelImage)
            }

            checkBox.isSelected = selectedSearchModels.contains(searchModel)
        }
    }

    interface OnItemClickListener {
        fun onSearchItemClicked(searchModel: SearchModel)
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
        holder.bindEvent(roomModel, itemClickListener)
    }

    fun update(data: MutableList<SearchModel>) {
        searchModels = data
        notifyDataSetChanged()
    }

    fun select(data: SearchModel) {
        selectedSearchModels.add(data)
        notifyDataSetChanged()
    }
}
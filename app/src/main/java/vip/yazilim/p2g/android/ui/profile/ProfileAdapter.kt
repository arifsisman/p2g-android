package vip.yazilim.p2g.android.ui.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.model.p2g.UserModel

/**
 * @author mustafaarifsisman - 31.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class ProfileAdapter(private var userModel: List<UserModel>) :
    RecyclerView.Adapter<ProfileAdapter.MViewHolder>() {

    private lateinit var view: View

    class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.profile_photo_image_view)
        val email: TextView = itemView.findViewById(R.id.email_text_view)
        val onlineStatus: ImageView = itemView.findViewById(R.id.online_status_image_view)
        val userName: TextView = itemView.findViewById(R.id.user_name_text_view)
        val friendCountsTextView: TextView = itemView.findViewById(R.id.friend_counts_text_view)
        val songAndRoomStatus: TextView = itemView.findViewById(R.id.song_room_status_text_view)
        val country: TextView = itemView.findViewById(R.id.country_text_view)
        val anthem: TextView = itemView.findViewById(R.id.anthem_text_view)
        val spotifyId: TextView = itemView.findViewById(R.id.spotify_id_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.row_profile_me, parent, false)
        return MViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userModel.size
    }

    fun update(data: List<UserModel>) {
        userModel = data
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        val userModel = userModel[position]
        val user = userModel.user

        if(user != null){
            Glide.with(view)
                .load(user.imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.profileImage)

            holder.userName.text = user.name
            holder.country.text = user.countryCode
            holder.email.text = user.email

            if (user.anthem == null) {
                holder.anthem.visibility = View.INVISIBLE
            } else {
                holder.anthem.visibility = View.VISIBLE
                holder.anthem.text = user.anthem
            }
            holder.spotifyId.text = user.id
            holder.friendCountsTextView.text = userModel.friends?.size.toString()
            if (userModel.room != null) {
                holder.songAndRoomStatus.text = "In" + userModel.room!!.name
            }
        }

    }
}
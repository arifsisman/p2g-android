package vip.yazilim.p2g.android.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.haipq.android.flagkit.FlagImageView
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.OnlineStatus
import vip.yazilim.p2g.android.model.p2g.FriendRequestModel
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.util.glide.GlideApp

/**
 * @author mustafaarifsisman - 31.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class ProfileAdapter(
    private var userModel: List<UserModel>,
    private var friendRequestModel: FriendRequestModel
) :
    RecyclerView.Adapter<ProfileAdapter.MViewHolder>() {

    private lateinit var view: View

    class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.profile_photo_image_view)
        val flagImage: FlagImageView = itemView.findViewById(R.id.country_flag_image_view)
        val email: TextView = itemView.findViewById(R.id.email_text_view)
        val statusOnline: ImageView = itemView.findViewById(R.id.online_status_online_image_view)
        val statusOffline: ImageView = itemView.findViewById(R.id.online_status_offline_image_view)
        val statusAway: ImageView = itemView.findViewById(R.id.online_status_offline_image_view)
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

    fun update(data: FriendRequestModel) {
        friendRequestModel = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        val userModel = userModel[position]
        val user = userModel.user

        if (user != null) {
            val profileNamePlaceholder = user.name
            val profileEmailPlaceholder =
                view.resources.getString(R.string.placeholder_email) + " " + user.email
            val profileSongAndRoomStatusPlaceholder =
                view.resources.getString(R.string.placeholder_song_and_room_status_helper) + " " + userModel.room?.name
            val profileAnthemPlaceholder =
                view.resources.getString(R.string.placeholder_anthem) + " " + user.anthem
            val profileSpotifyAccountIdPlaceholder =
                view.resources.getString(R.string.placeholder_spotify_account_id) + " " + user.id
            val profileCountryPlaceholder =
                view.resources.getString(R.string.placeholder_country) + " " + user.countryCode

            if (user.imageUrl != null) {
                GlideApp.with(view)
                    .load(user.imageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.profileImage)
            }

            try {
                holder.flagImage.countryCode = user.countryCode
            } catch (exception: Exception) {
                holder.flagImage.visibility = View.INVISIBLE
            }

            holder.userName.text = profileNamePlaceholder
            holder.country.text = profileCountryPlaceholder
            holder.email.text = profileEmailPlaceholder

            if (user.anthem == null) {
                holder.anthem.visibility = View.INVISIBLE
            } else {
                holder.anthem.visibility = View.VISIBLE
                holder.anthem.text = profileAnthemPlaceholder
            }

            holder.spotifyId.text = profileSpotifyAccountIdPlaceholder

            if (userModel.room != null) {
                holder.songAndRoomStatus.text = profileSongAndRoomStatusPlaceholder
            } else {
                val songAndRoomStatusString =
                    view.resources.getString(R.string.placeholder_room_user_not_found)
                holder.songAndRoomStatus.text = songAndRoomStatusString
            }

            when (user.onlineStatus) {
                OnlineStatus.ONLINE.onlineStatus -> {
                    holder.statusOnline.visibility = View.VISIBLE
                }
                OnlineStatus.OFFLINE.onlineStatus -> {
                    holder.statusOffline.visibility = View.VISIBLE
                }
                OnlineStatus.AWAY.onlineStatus -> {
                    holder.statusAway.visibility = View.VISIBLE
                }
            }
        }

        if (friendRequestModel.friends != null) {
            val profileFriendCountsPlaceholder =
                friendRequestModel.friends?.size.toString() + " " + view.resources.getString(R.string.placeholder_friend_counts)
            holder.friendCountsTextView.text = profileFriendCountsPlaceholder
        } else {
            holder.friendCountsTextView.visibility = View.INVISIBLE
        }
    }

}
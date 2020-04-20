package vip.yazilim.p2g.android.ui.main.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_profile.view.*
import kotlinx.android.synthetic.main.item_room.view.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.OnlineStatus
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.util.glide.GlideApp
import vip.yazilim.p2g.android.util.helper.RoomHelper
import vip.yazilim.p2g.android.util.helper.TimeHelper.Companion.getFormattedFull
import vip.yazilim.p2g.android.util.helper.TimeHelper.Companion.toZonedDateTime

/**
 * @author mustafaarifsisman - 31.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class ProfileAdapter(
    private var userModel: UserModel?,
    private var roomModel: RoomModel?,
    private var friendCounts: Int,
    private val isMe: Boolean
) :
    RecyclerView.Adapter<ProfileAdapter.MViewHolder>() {

    private lateinit var view: View

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(userModel: UserModel, roomModel: RoomModel?) {
            val user = userModel.user
            val room = roomModel?.room
            val song = roomModel?.song

            val profileNamePlaceholder = user.name
            val memberSincePlaceholder =
                "${view.resources.getString(R.string.placeholder_member_since)} ${user.creationDate.toZonedDateTime()
                    .getFormattedFull()}"
            val profileEmailPlaceholder =
                "${view.resources.getString(R.string.placeholder_email)} ${user.email}"
            val profileSpotifyAccountIdPlaceholder =
                "${view.resources.getString(R.string.placeholder_spotify_account_id)} ${user.id}"

            if (user.imageUrl != null) {
                GlideApp.with(view)
                    .load(user.imageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(itemView.profile_photo)
            } else {
                itemView.profile_photo.setImageResource(R.drawable.ic_profile_image)
            }

            try {
                itemView.user_country_flag.countryCode = user.countryCode
            } catch (exception: Exception) {
                itemView.user_country_flag.visibility = View.GONE
            }

            itemView.user_name.text = profileNamePlaceholder
            itemView.member_since.text = memberSincePlaceholder

            when (user.onlineStatus) {
                OnlineStatus.ONLINE.onlineStatus -> {
                    itemView.online_status.setImageResource(android.R.drawable.presence_online)
                    itemView.online_status.visibility = View.VISIBLE
                }
                OnlineStatus.OFFLINE.onlineStatus -> {
                    itemView.online_status.setImageResource(android.R.drawable.presence_offline)
                    itemView.online_status.visibility = View.VISIBLE
                }
                OnlineStatus.AWAY.onlineStatus -> {
                    itemView.online_status.setImageResource(android.R.drawable.presence_away)
                    itemView.online_status.visibility = View.VISIBLE
                }
            }

            val profileFriendCountsPlaceholder =
                "$friendCounts ${view.resources.getString(R.string.placeholder_friend_counts)}"
            itemView.friend_counts.text = profileFriendCountsPlaceholder

            // Room
            if (room != null) {
                val roomOwnerPlaceholder =
                    "${view.resources.getString(R.string.placeholder_room_owner)} ${userModel.roomModel?.owner?.name}"

                itemView.roomName.text = room.name
                itemView.roomOwner.text = roomOwnerPlaceholder
                itemView.userCount.text = userModel.roomModel?.userCount.toString()

                if (room.privateFlag) {
                    itemView.lockImage.visibility = View.VISIBLE
                } else {
                    itemView.lockImage.visibility = View.GONE
                }

                itemView.country_flag.visibility = View.GONE

                if (song != null) {
                    if (song.imageUrl != null) {
                        GlideApp.with(view)
                            .load(song.imageUrl)
                            .into(itemView.song_image)
                    }

                    itemView.song_name.text = song.songName
                    itemView.song_artists.text =
                        RoomHelper.getArtistsPlaceholder(song.artistNames, "")
                    itemView.seek_bar.max = song.durationMs
                    itemView.seek_bar.progress = RoomViewModel.getCurrentSongMs(song)

                    itemView.song_status.visibility = View.VISIBLE
                } else {
                    itemView.song_status.visibility = View.GONE
                }
            } else {
                itemView.room.visibility = View.GONE
                itemView.divider.visibility = View.GONE
            }


            // User public/private
            if (isMe) {
                itemView.user_public.visibility = View.VISIBLE
                itemView.user_private.visibility = View.VISIBLE
                itemView.email.text = profileEmailPlaceholder
                itemView.spotify_id.text = profileSpotifyAccountIdPlaceholder
            } else {
                itemView.user_public.visibility = View.VISIBLE
                itemView.user_private.visibility = View.GONE
            }

            itemView.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile, parent, false)
        return MViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 1
    }

    fun update(data: UserModel) {
        userModel = data
        notifyDataSetChanged()
    }

    fun update(data: Int) {
        friendCounts = data
        notifyDataSetChanged()
    }

    fun update(data: RoomModel) {
        roomModel = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        userModel?.let { holder.bindView(it, roomModel) }
    }

}
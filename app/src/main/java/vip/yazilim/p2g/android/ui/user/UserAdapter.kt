package vip.yazilim.p2g.android.ui.user

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.haipq.android.flagkit.FlagImageView
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.OnlineStatus
import vip.yazilim.p2g.android.model.p2g.*
import vip.yazilim.p2g.android.util.glide.GlideApp
import vip.yazilim.p2g.android.util.helper.RoomHelper
import vip.yazilim.p2g.android.util.helper.TimeHelper

/**
 * @author mustafaarifsisman - 11.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class UserAdapter(
    private var userModel: UserModel?,
    private var roomModel: RoomModelSimplified?,
    private var friends: MutableList<FriendModel>
) :
    RecyclerView.Adapter<UserAdapter.MViewHolder>() {

    private lateinit var view: View

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.user_card_view)
        private val memberSince: TextView = itemView.findViewById(R.id.member_since_text_view)
        private val profileImage: ImageView = itemView.findViewById(R.id.profile_photo_image_view)
        private val flagImage: FlagImageView = itemView.findViewById(R.id.country_flag_image_view)
        private val onlineStatus: ImageView =
            itemView.findViewById(R.id.online_status_online_image_view)
        private val userName: TextView = itemView.findViewById(R.id.user_name_text_view)
        private val friendCountsTextView: TextView =
            itemView.findViewById(R.id.friend_counts_text_view)
        private val songAndRoomStatus: TextView =
            itemView.findViewById(R.id.song_room_status_text_view)
        private val anthem: TextView = itemView.findViewById(R.id.anthem_text_view)

        fun bindView(user: User?, room: Room?) {
            if (user != null) {
                val profileNamePlaceholder = user.name
                val profileSongAndRoomStatusPlaceholder =
                    "${view.resources.getString(R.string.placeholder_song_and_room_status_helper)} ${room?.name}"
                val profileAnthemPlaceholder =
                    "${view.resources.getString(R.string.placeholder_anthem)} ${user.anthem}"
                val memberSincePlaceholder =
                    "${view.resources.getString(R.string.placeholder_member_since)} ${user.creationDate?.format(
                        TimeHelper.dateTimeFormatterFull
                    )}"

                if (user.imageUrl != null) {
                    GlideApp.with(view)
                        .load(user.imageUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profileImage)
                } else {
                    profileImage.setImageResource(R.drawable.ic_profile_image)
                }

                try {
                    flagImage.countryCode = user.countryCode
                } catch (exception: Exception) {
                    flagImage.visibility = View.INVISIBLE
                }

                userName.text = profileNamePlaceholder
                memberSince.text = memberSincePlaceholder

                if (user.anthem == null) {
                    anthem.visibility = View.INVISIBLE
                } else {
                    anthem.visibility = View.VISIBLE
                    anthem.text = profileAnthemPlaceholder
                }


                when {
                    roomModel == null -> {
                        songAndRoomStatus.text = profileSongAndRoomStatusPlaceholder
                    }
                    room == null -> {
                        val songAndRoomStatusString =
                            view.resources.getString(R.string.placeholder_room_user_not_found)
                        songAndRoomStatus.text = songAndRoomStatusString
                    }
                    roomModel != null -> {
                        val tempText =
                            "$profileSongAndRoomStatusPlaceholder " + RoomHelper.getRoomSongStatus(
                                view,
                                roomModel?.song
                            )
                        songAndRoomStatus.text = tempText
                    }
                }

                when (user.onlineStatus) {
                    OnlineStatus.ONLINE.onlineStatus -> {
                        onlineStatus.setImageResource(android.R.drawable.presence_online)
                        onlineStatus.visibility = View.VISIBLE
                    }
                    OnlineStatus.OFFLINE.onlineStatus -> {
                        onlineStatus.setImageResource(android.R.drawable.presence_offline)
                        onlineStatus.visibility = View.VISIBLE
                    }
                    OnlineStatus.AWAY.onlineStatus -> {
                        onlineStatus.setImageResource(android.R.drawable.presence_away)
                        onlineStatus.visibility = View.VISIBLE
                    }
                }

                cardView.visibility = View.VISIBLE
            }

            if (friends.isNotEmpty()) {
                val profileFriendCountsPlaceholder =
                    "${friends.size} ${view.resources.getString(R.string.placeholder_friend_counts)}"
                friendCountsTextView.text = profileFriendCountsPlaceholder
            } else {
                val profileFriendCountsPlaceholder =
                    "0 " + view.resources.getString(R.string.placeholder_friend_counts)
                friendCountsTextView.text = profileFriendCountsPlaceholder
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.row_user, parent, false)
        return MViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 1
    }

    fun update(data: RoomModelSimplified) {
        roomModel = data
        notifyDataSetChanged()
    }

    fun update(data: MutableList<FriendModel>) {
        friends = data
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.bindView(userModel?.user, userModel?.room)
    }

}
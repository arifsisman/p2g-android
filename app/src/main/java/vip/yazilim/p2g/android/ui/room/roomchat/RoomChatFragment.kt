package vip.yazilim.p2g.android.ui.room.roomchat

import com.bumptech.glide.request.RequestOptions
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.fragment_room_chat.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.model.p2g.ChatMessage
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.util.glide.GlideApp


/**
 * @author mustafaarifsisman - 10.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomChatFragment(var roomViewModel: RoomViewModel) :
    FragmentBase(roomViewModel, R.layout.fragment_room_chat) {

    private var senderId: String? = roomViewModel.roomUserModel.value?.roomUser?.userId

    override fun setupUI() {
        val imageLoader = ImageLoader { imageView, url, payload ->
            GlideApp.with(imageView)
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView)
        }

        val adapter: MessagesListAdapter<ChatMessage> =
            MessagesListAdapter<ChatMessage>(senderId, imageLoader)
        messagesList.setAdapter(adapter)
    }

    override fun setupViewModel() {
    }
}
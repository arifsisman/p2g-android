package vip.yazilim.p2g.android.ui.room.roomchat

import com.bumptech.glide.request.RequestOptions
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.fragment_room_chat.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.model.p2g.ChatMessage
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.util.glide.GlideApp
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author mustafaarifsisman - 10.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomChatFragment(var roomViewModel: RoomViewModel) :
    FragmentBase(roomViewModel, R.layout.fragment_room_chat) {

    private var senderId: String? = roomViewModel.roomUserModel.value?.roomUser?.userId
    private lateinit var messagesListAdapter: MessagesListAdapter<ChatMessage>
    private lateinit var messageInput: MessageInput

    override fun setupUI() {
        val imageLoader = ImageLoader { imageView, url, payload ->
            GlideApp.with(imageView)
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView)
        }

        messagesListAdapter = MessagesListAdapter<ChatMessage>(senderId, imageLoader)
        messagesList.setAdapter(messagesListAdapter)

        messageInput = input
        messageInput.setInputListener { input ->
            return@setInputListener messageInputHandler(input)
        }

    }

    override fun setupViewModel() {
    }

    private fun messageInputHandler(input: CharSequence): Boolean {
        messagesListAdapter.addToStart(
            ChatMessage(
                roomViewModel.roomUserModel.value?.roomUser,
                input.toString(),
                Date()
            ), true
        )

        return true
    }

    private fun getMessageStringFormatter(): MessagesListAdapter.Formatter<ChatMessage>? {
        return MessagesListAdapter.Formatter<ChatMessage> { message ->
            val createdAt = SimpleDateFormat(
                "MMM d, EEE 'at' h:mm a",
                Locale.getDefault()
            )
                .format(message.createdAt)

            var text: String? = message.text
            if (text == null) text = "[attachment]"
            String.format(
                Locale.getDefault(), "%s: %s (%s)",
                message.user?.name, text, createdAt
            )
        }
    }

}
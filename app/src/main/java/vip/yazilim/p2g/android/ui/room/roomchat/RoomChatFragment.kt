package vip.yazilim.p2g.android.ui.room.roomchat

import android.content.Intent
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.fragment_room_chat.*
import kotlinx.android.synthetic.main.row_incoming_message.view.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_MESSAGE_SEND
import vip.yazilim.p2g.android.model.p2g.ChatMessage
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.util.glide.GlideApp
import vip.yazilim.p2g.android.util.helper.TimeHelper
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author mustafaarifsisman - 10.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomChatFragment(var roomViewModel: RoomViewModel) :
    FragmentBase(roomViewModel, R.layout.fragment_room_chat) {

    private var senderId: String? = roomViewModel.roomUserModel.value?.roomUser?.userId
    private lateinit var messagesAdapter: MessagesListAdapter<ChatMessage>
    private lateinit var messageInput: MessageInput

    override fun setupUI() {
        val imageLoader = ImageLoader { imageView, url, payload ->
            GlideApp.with(imageView)
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView)
        }

        val holdersConfig = MessageHolders()
        holdersConfig.setIncomingTextLayout(R.layout.row_incoming_message)

        messagesAdapter = MessagesListAdapter<ChatMessage>(senderId, holdersConfig, imageLoader)
        messagesList.setAdapter(messagesAdapter)

        messageInput = input
        messageInput.setInputListener { input ->
            return@setInputListener messageInputHandler(input)
        }

    }

    override fun setupViewModel() {
        roomViewModel.newMessage.observe(this, renderNewMessage)
    }

    override fun onResume() {
        super.onResume()
        loadMessages()
    }

    private fun messageInputHandler(input: CharSequence): Boolean {
        val chatMessage = ChatMessage(
            roomViewModel.roomUserModel.value?.roomUser,
            input.toString(),
            TimeHelper.getLocalDateTimeZonedUTC()
        )
        sendMessageToWebSocket(chatMessage)
        return true
    }

    private fun loadMessages() {
        Handler().postDelayed({
            val messages: MutableList<ChatMessage>? = roomViewModel.messages
            messages?.forEach {
                messagesAdapter.upsert(it, true)
            }
        }, 50)
    }

    private fun sendMessageToWebSocket(chatMessage: ChatMessage) {
        val intent = Intent()
        intent.action = ACTION_MESSAGE_SEND
        intent.putExtra(ACTION_MESSAGE_SEND, chatMessage)
        activity?.sendBroadcast(intent)
    }

    private val renderNewMessage = Observer<ChatMessage> { chatMessage ->
        roomViewModel.messages.add(chatMessage)
        messagesAdapter.addToStart(chatMessage, true)
    }

    inner class CustomOutcomingMessageViewHolder(itemView: View?, payload: Any?) :
        MessageHolders.OutcomingTextMessageViewHolder<ChatMessage?>(itemView, payload) {
        fun onBind(chatMessage: ChatMessage) {
            super.onBind(chatMessage)
            itemView.messageAuthor.text = chatMessage.roomUser?.name ?: ""
        }
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
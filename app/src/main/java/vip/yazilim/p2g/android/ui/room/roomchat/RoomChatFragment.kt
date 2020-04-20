package vip.yazilim.p2g.android.ui.room.roomchat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.request.RequestOptions
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.fragment_room_chat.*
import kotlinx.android.synthetic.main.item_incoming_message.view.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_MESSAGE_SEND
import vip.yazilim.p2g.android.model.p2g.ChatMessage
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.util.glide.GlideApp
import vip.yazilim.p2g.android.util.helper.TimeHelper


/**
 * @author mustafaarifsisman - 10.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomChatFragment :
    FragmentBase(R.layout.fragment_room_chat) {

    private lateinit var messagesAdapter: MessagesListAdapter<ChatMessage>
    private lateinit var senderId: String
    private lateinit var roomViewModel: RoomViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomViewModel = ViewModelProvider(activity as RoomActivity).get(RoomViewModel::class.java)
        senderId = roomViewModel.roomUserModel.value?.roomUser?.userId.toString()
    }

    override fun setupUI() {
        val imageLoader = ImageLoader { imageView, url, _ ->
            GlideApp.with(imageView)
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView)
        }

        val holdersConfig = MessageHolders()
        holdersConfig.setIncomingTextLayout(R.layout.item_incoming_message)
        holdersConfig.setIncomingTextHolder(
            CustomIncomingMessageViewHolder::class.java,
            R.layout.item_incoming_message
        )

        messagesAdapter = MessagesListAdapter<ChatMessage>(senderId, holdersConfig, imageLoader)
        messages_list.setAdapter(messagesAdapter)

        input.setInputListener { input ->
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

    class CustomIncomingMessageViewHolder(itemView: View?, payload: Any?) :
        MessageHolders.IncomingTextMessageViewHolder<ChatMessage?>(itemView, payload) {
        override fun onBind(message: ChatMessage?) {
            super.onBind(message)
            itemView.message_author.text = message?.roomUser?.name ?: ""
        }
    }

}
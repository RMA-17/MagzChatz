package com.rmaprojects.magzchatz.pages.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.rmaprojects.magzchatz.R
import com.rmaprojects.magzchatz.databinding.ItemChatBinding
import com.rmaprojects.magzchatz.model.Chat
import com.rmaprojects.magzchatz.model.User
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(private val listChat: List<Chat?>, private val currentUser: User?) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {


    var onLongClickListener: ((Chat?) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChatViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
    )

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = listChat[position]
        holder.bindView(chat, currentUser, onLongClickListener)
    }

    override fun getItemCount() = listChat.size

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var binding: ItemChatBinding

        fun bindView(chat: Chat?, currentUser: User?, onLongClickListener: ((Chat?) -> Unit)?) {
            binding = ItemChatBinding.bind(itemView)
            if (chat?.senderId != currentUser?.idUser) {
                binding.chatReceiver.isVisible = false
                binding.chatSender.isVisible = true
                binding.senderBubble.editText?.setText(chat?.message)
                binding.txtClockSender.text = SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.getDefault()
                ).format(Date(chat?.timestamp!!.toString().toLong()))
            } else {
                binding.chatReceiver.isVisible = true
                binding.chatSender.isVisible = false
                binding.receiverBubble.editText?.setText(chat?.message)
                binding.txtClockReceiver.text = SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.getDefault()
                ).format(Date(chat?.timestamp!!.toString().toLong()))
                binding.chatReceiver.setOnLongClickListener {
                    onLongClickListener?.invoke(chat)
                    true
                }
            }
        }

    }
}
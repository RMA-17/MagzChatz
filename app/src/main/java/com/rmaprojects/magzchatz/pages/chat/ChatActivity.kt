package com.rmaprojects.magzchatz.pages.chat

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rmaprojects.magzchatz.R
import com.rmaprojects.magzchatz.databinding.ActivityChatBinding
import com.rmaprojects.magzchatz.model.Chat
import com.rmaprojects.magzchatz.model.User
import com.rmaprojects.magzchatz.util.showSnackBar
import com.rmaprojects.magzchatz.viewmodel.UserViewModel

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val viewModel: UserViewModel by viewModels()

    private val database by lazy {
        Firebase.database
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val senderUser = intent.getParcelableExtra<User>(SENDER_USER)
        val messageContent = binding.inputMessage.editText

        binding.chatToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.imgUserProfile.load(if (senderUser?.imageProfile.isNullOrEmpty()) R.drawable.ic_baseline_person_24 else senderUser?.imageProfile)
        binding.txtUserName.text = senderUser?.username

        viewModel.currentUserData.observe(this) { currentUser ->
            val getRoomChat =
                if (currentUser?.idUser.toString() < senderUser?.idUser.toString())
                    "${currentUser?.idUser}${senderUser?.idUser}"
                else "${senderUser?.idUser}${currentUser?.idUser}"

            database.getReference("chats").child(getRoomChat).orderByChild("timestamp")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val listChat =
                            snapshot.children.map {
                                it.getValue(Chat::class.java)
                            }

                        val adapter = ChatAdapter(listChat, currentUser)
                        binding.recyclerViewChat.adapter = adapter
                        binding.recyclerViewChat.layoutManager =
                            LinearLayoutManager(this@ChatActivity).apply { stackFromEnd = true }
                        if (listChat.isNotEmpty()) binding.recyclerViewChat.smoothScrollToPosition(listChat.lastIndex)

                        val listOfOptions = arrayOf(
                            "Hapus",
                            "Salin",
                            "Bagikan",
                        )

                        val chatReference = database.getReference("chats")
                            .child(getRoomChat)

                        adapter.onLongClickListener = { chat ->
                            MaterialAlertDialogBuilder(this@ChatActivity)
                                .setItems(listOfOptions) { _, which ->
                                    when (which) {
                                        DELETE_CHAT -> deleteChat(chat, chatReference)
                                        COPY_CHAT -> copyChat(chat)
                                        SHARE_CHAT -> shareChat(chat)
                                    }
                                }
                                .create().show()
                        }

                        binding.btnSend.setOnClickListener {
                            if (messageContent?.text.isNullOrEmpty() || messageContent?.text.isNullOrBlank()) {
                                return@setOnClickListener
                            }

                            pushChat(chatReference, messageContent, currentUser, senderUser)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("ERROR", error.toString())
                    }

                })
        }

    }

    private fun shareChat(chat: Chat?) {

    }

    private fun copyChat(chat: Chat?) {

    }

    private fun deleteChat(chat: Chat?, chatReference: DatabaseReference) {
        chatReference.orderByChild("chatId").equalTo(chat?.chatId.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (chatData in snapshot.children) {
                        chatData.ref.removeValue()
                    }
                    showSnackBar(binding.root, "Berhasil menghapus pesan!", Snackbar.LENGTH_SHORT)
                }

                override fun onCancelled(error: DatabaseError) {
                    showSnackBar(binding.root, "Gagal menghapus pesan!", Snackbar.LENGTH_SHORT)
                }

            })
    }

    private fun pushChat(
        chatReference: DatabaseReference,
        messageContent: EditText?,
        currentUser: User?,
        senderUser: User?
    ) {
        val key = chatReference.push().key.toString()
        val chat = Chat(
            chatId = key,
            message = messageContent?.text.toString(),
            receiverId = senderUser?.idUser,
            senderId = currentUser?.idUser,
        )
        chatReference.push().setValue(chat)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    showSnackBar(binding.root, "Pesan tidak terkirim", Snackbar.LENGTH_SHORT)
                }
                messageContent?.setText("")
            }
    }

    companion object {
        const val SENDER_USER = "VALUESENDERUSER"
        const val DELETE_CHAT = 0
        const val COPY_CHAT = 1
        const val SHARE_CHAT = 2
    }
}
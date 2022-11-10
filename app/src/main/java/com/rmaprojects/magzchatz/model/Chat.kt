package com.rmaprojects.magzchatz.model


import com.google.firebase.database.ServerValue

data class Chat(
    val chatId : String? = "",
    val message:String? = "",
    val senderId:String? = "",
    val receiverId: String? = "",
    val timestamp: Any? = ServerValue.TIMESTAMP,
)

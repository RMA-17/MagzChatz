package com.rmaprojects.magzchatz.pages.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.rmaprojects.magzchatz.R
import com.rmaprojects.magzchatz.databinding.ItemChatPersonBinding
import com.rmaprojects.magzchatz.model.User

class UserAdapter(private val userList: List<User?>) : RecyclerView.Adapter<UserAdapter.UserAdapterViewHolder>() {

    var chatThePerson: ((User?) -> Unit)?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapterViewHolder
        = UserAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_person, parent, false))

    override fun onBindViewHolder(holder: UserAdapterViewHolder, position: Int) {
        val user = userList[position]
        holder.bindView(user)
        holder.binding.root.setOnClickListener {
            chatThePerson?.invoke(user)
        }
    }

    override fun getItemCount() = userList.size

    class UserAdapterViewHolder(private val view:View) : RecyclerView.ViewHolder(view) {

        lateinit var binding : ItemChatPersonBinding

        fun bindView(user: User?) {
            binding = ItemChatPersonBinding.bind(view)

            user?.let {
                binding.txtUserName.text = it.username
                binding.imgUser.load( if (it.imageProfile.isNullOrEmpty()) R.drawable.ic_baseline_person_24 else it.imageProfile )
            }

        }
    }
}
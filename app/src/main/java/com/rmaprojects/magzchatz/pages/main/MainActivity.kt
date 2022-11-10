package com.rmaprojects.magzchatz.pages.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rmaprojects.magzchatz.R
import com.rmaprojects.magzchatz.databinding.ActivityMainBinding
import com.rmaprojects.magzchatz.model.User
import com.rmaprojects.magzchatz.pages.LoginActivity
import com.rmaprojects.magzchatz.pages.ProfileActivity
import com.rmaprojects.magzchatz.pages.chat.ChatActivity
import com.rmaprojects.magzchatz.util.showSnackBar

class MainActivity : AppCompatActivity() {

    private val auth by lazy {
        Firebase.auth
    }

    private val database by lazy {
        Firebase.database
    }

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (auth.currentUser == null) return

        database.getReference("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listUsers = snapshot.children.map {
                        it.getValue(User::class.java)
                    }

                    val adapter = UserAdapter(listUsers.filter { user -> user?.email != auth.currentUser?.email })
                    binding.recyclerView.adapter = adapter
                    binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

                    adapter.chatThePerson = {
                        startActivity(
                            Intent(this@MainActivity, ChatActivity::class.java).putExtra(ChatActivity.SENDER_USER, it)
                        )
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    showSnackBar(binding.root, error.message, Snackbar.LENGTH_LONG)
                }

            })

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
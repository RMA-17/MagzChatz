package com.rmaprojects.magzchatz.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.rmaprojects.magzchatz.model.User

class UserViewModel : ViewModel() {

    private val database by lazy {
        Firebase.database
    }

    private val auth by lazy {
        Firebase.auth
    }

    private val _currentUserData = MutableLiveData<User?>()
    val currentUserData : LiveData<User?> = _currentUserData

    init {
        getCurrentUserData()
    }

    private fun getCurrentUserData() {
        database.getReference("users")
            .child(auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue<User>()
                    _currentUserData.postValue(user)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("ERROR", error.toString())
                }
            })
    }
}
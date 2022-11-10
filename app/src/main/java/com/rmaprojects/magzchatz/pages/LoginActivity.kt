package com.rmaprojects.magzchatz.pages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.rmaprojects.magzchatz.databinding.ActivityLoginBinding
import com.rmaprojects.magzchatz.pages.main.MainActivity
import com.rmaprojects.magzchatz.util.showSnackBar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val firebaseAuth by lazy { Firebase.auth }
    private val firebaseDatabase by lazy { Firebase.database }
    private val fcm by lazy { Firebase.messaging }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.inputEmail.editText?.text.toString().trim()
            val password = binding.inputPassword.editText?.text.toString().trim()
            if (email.isEmpty()) {
                binding.inputEmail.error = "Email belum diisi"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.inputPassword.error = "Password belum diisi"
                return@setOnClickListener
            }

            Log.d("EMAIL", email)
            Log.d("PASSWORD", password)

            firebaseAuth.signInWithEmailAndPassword(
                email,
                password
            ).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("ERROR", task.exception.toString())
                    showSnackBar(
                        binding.root,
                        "Password atau email anda salah, awikwok",
                        Snackbar.LENGTH_SHORT
                    )
                    return@addOnCompleteListener
                }
                val user = task.result.user ?: return@addOnCompleteListener
                applyTokenToUser(user)
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun applyTokenToUser(user: FirebaseUser) {
        fcm.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(
                    "ERROR",
                    "Fetching FCM registration token failed",
                    task.exception
                )
                return@addOnCompleteListener
            }

            val token = task.result

            firebaseDatabase.getReference("users")
                .child(user.uid)
                .child("token")
                .setValue(token)
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }
}
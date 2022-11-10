package com.rmaprojects.magzchatz.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rmaprojects.magzchatz.R
import com.rmaprojects.magzchatz.databinding.ActivityRegisterBinding
import com.rmaprojects.magzchatz.model.User
import com.rmaprojects.magzchatz.util.showSnackBar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseDb: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseDb = Firebase.database
        firebaseAuth = Firebase.auth

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        binding.btnRegister.setOnClickListener {
            registerUser()
        }

    }

    private fun registerUser(
    ) = with(binding) {
        if (inputFullName.editText?.text.toString().isEmpty()) {
            inputFullName.error = "Nama lengkap belum diisi"
            return@with
        }

        if (inputPhoneNumber.editText?.text.toString().isEmpty()) {
            inputPhoneNumber.error = "Nomor telepon belum diisi"
            return@with
        }

        if (inputEmail.editText?.text.toString().isEmpty()) {
            inputEmail.error = "Email belum diisi"
            return@with
        }

        if (inputPassword.editText?.text.toString().isEmpty()) {
            inputPassword.error = "Password belum diisi"
            return@with
        }

        if (inputConfirmPassword.editText?.text.toString() != inputPassword.editText?.text.toString()) {
            inputConfirmPassword.error = "Konfirmasi harus sama dengan password"
            return@with
        }

        firebaseAuth.createUserWithEmailAndPassword(
            inputEmail.editText?.text.toString(),
            inputPassword.editText?.text.toString()
        )
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    showSnackBar(binding.root, "Register Gagal!", Snackbar.LENGTH_SHORT)
                    Log.d("ERROR", it.toString())
                    return@addOnCompleteListener
                }

                val user = it.result.user ?: return@addOnCompleteListener

                inputNewUser(user)

                MaterialAlertDialogBuilder(this@RegisterActivity)
                    .setTitle("Berhasil!")
                    .setMessage("Berhasil membuat user baru, silahkan login!")
                    .setIcon(R.drawable.ic_baseline_check_circle_24)
                    .setPositiveButton("Kembali ke login") { _, _ -> finish() }
                    .setCancelable(false)
                    .create().show()
            }
    }

    private fun inputNewUser(user: FirebaseUser) = with(binding) {
        val newUser = User(
            email = user.email!!,
            idUser = user.uid,
            phoneNumber = inputPhoneNumber.editText?.text?.trim().toString().toLong(),
            username = inputFullName.editText?.text.toString(),
        )

        firebaseDb.getReference("users")
            .child(user.uid)
            .setValue(newUser)
    }
}
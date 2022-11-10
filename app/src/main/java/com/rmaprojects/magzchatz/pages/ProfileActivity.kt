package com.rmaprojects.magzchatz.pages

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.rmaprojects.magzchatz.R
import com.rmaprojects.magzchatz.databinding.ActivityProfileBinding
import com.rmaprojects.magzchatz.util.showSnackBar
import com.rmaprojects.magzchatz.viewmodel.UserViewModel

class ProfileActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModels()
    private lateinit var binding: ActivityProfileBinding
    private val auth by lazy {
        Firebase.auth
    }
    private val database by lazy {
        Firebase.database
    }
    private val storage by lazy {
        Firebase.storage
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressIndicator.isVisible = false

        viewModel.currentUserData.observe(this) { user ->
            binding.txtBio.text = if (user?.bio.isNullOrEmpty()) "Tidak ada Bio" else user?.bio
            binding.txtUsername.text = user?.username
            binding.circleImageView.load(if (user?.imageProfile.isNullOrEmpty()) R.drawable.ic_baseline_person_24 else user?.imageProfile)
            binding.btnLogOut.setOnClickListener {
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            binding.btnBioEdit.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(BottomSheetChangeBio.GET_BIO, user?.bio)
                val editBioBs = BottomSheetChangeBio().apply {
                    arguments = bundle
                }
                editBioBs.show(supportFragmentManager, "BottomSheetLayout")
            }
            binding.circleImageView.setOnClickListener {
                ImagePicker.with(this)
                    .galleryOnly()
                    .galleryMimeTypes(
                        arrayOf(
                            "image/png",
                            "image/pg",
                            "image/jpeg"
                        )
                    )
                    .compress(1000)
                    .start(PICK_FROM_GALLERY)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri = data?.data!!
                when (requestCode) {
                    PICK_FROM_GALLERY -> {
                        storage.reference.child("images").child(viewModel.currentUserData.value?.idUser.toString())
                            .putFile(uri)
                            .addOnProgressListener {
                                val percentage = it.bytesTransferred.toFloat() / it.totalByteCount.toFloat() * 100
                                binding.progressIndicator.isVisible = true
                                binding.progressIndicator.setProgressCompat(percentage.toInt(), true)
                            }
                            .addOnCompleteListener { task ->
                                binding.progressIndicator.isVisible = false
                                if (!task.isSuccessful) {
                                    showSnackBar(
                                        binding.root,
                                        "Gambar gagal di upload",
                                        Snackbar.LENGTH_SHORT
                                    )
                                }
                                task.result.metadata?.reference?.downloadUrl?.addOnCompleteListener {
                                    database.getReference("users")
                                        .child(auth.currentUser?.uid!!).child("imageProfile")
                                        .setValue(it.result.toString()).addOnCompleteListener { task ->
                                            if (!task.isSuccessful) {
                                                showSnackBar(
                                                    binding.root,
                                                    "Gambar gagal di upload!",
                                                    Snackbar.LENGTH_SHORT
                                                )
                                            }
                                            showSnackBar(
                                                binding.root,
                                                "Gambar berhasil di upload!",
                                                Snackbar.LENGTH_SHORT
                                            )
                                        }
                                }

                            }
                    }
                }
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object {
        const val PICK_FROM_GALLERY = 101
    }


}
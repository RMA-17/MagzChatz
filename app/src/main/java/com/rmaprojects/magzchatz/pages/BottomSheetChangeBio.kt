package com.rmaprojects.magzchatz.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rmaprojects.magzchatz.R
import com.rmaprojects.magzchatz.databinding.BsUpdateBioBinding

class BottomSheetChangeBio : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bs_update_bio, container, false)
    }

    private val database by lazy {
        Firebase.database
    }
    private val auth by lazy {
        Firebase.auth
    }

    private var binding : BsUpdateBioBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = BsUpdateBioBinding.bind(view)
        val bio = arguments?.getString(GET_BIO) ?: ""

        binding!!.inputEditBio.editText?.setText(bio)

        binding!!.btnSaveBio.setOnClickListener {
            database.getReference("users").child(auth.currentUser!!.uid)
                .child("bio").setValue(binding!!.inputEditBio.editText?.text.toString()).addOnCompleteListener {
                    dismiss()
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val GET_BIO = "GETBIOVALUE"
    }
}
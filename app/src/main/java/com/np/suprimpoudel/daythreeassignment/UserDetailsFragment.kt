package com.np.suprimpoudel.daythreeassignment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.np.suprimpoudel.daythreeassignment.databinding.FragmentUserDetailsBinding
import com.np.suprimpoudel.daythreeassignment.features.shared.model.User
import com.np.suprimpoudel.daythreeassignment.network.FirebaseService
import com.np.suprimpoudel.daythreeassignment.utils.FirebaseConstants

class UserDetailsFragment : Fragment() {

    private lateinit var binding: FragmentUserDetailsBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userData: DatabaseReference
    private lateinit var user: User
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentUserDetailsBinding.bind(view)

        initDialog()
        initListener()
        initFirebase()

        dialog.show()

        userData = firebaseDatabase
            .reference.child(FirebaseConstants.DATA_USERS)

        populateData(userData)
    }

    private fun initDialog() {
        this.dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog)
        dialog.setCancelable(false)
    }

    private fun initFirebase() {
        firebaseDatabase = FirebaseService.getFirebaseDatabase()
        firebaseAuth = FirebaseService.getFirebaseAuth()
    }

    private fun populateData(userData: DatabaseReference) {
        userData.child(firebaseAuth.currentUser?.uid!!).addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)!!

                dialog.dismiss()

                binding.edtUserFullName.setText(user.fullName)
                binding.edtUserEmailAddress.setText(user.email)
                binding.edtUserPhoneNumber.setText(user.phoneNumber)

                Glide.with(requireView())
                    .load(user.profileImageUrl)
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.ic_baseline_person_24)
                    .into(binding.imgUserProfilePicture)

            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
                Toast.makeText(context, "Error Fetching User Info", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        })
    }

    private fun initListener() {
        binding.btnUpdateProfile.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile() {
        val email = binding.edtUserEmailAddress.text.toString()
        val phoneNumber = binding.edtUserPhoneNumber.text.toString()
        val fullName = binding.edtUserFullName.text.toString()

        if(email.isNotEmpty() && phoneNumber.isNotEmpty() && fullName.isNotEmpty()) {
            dialog.show()
            firebaseAuth.currentUser?.updateEmail(binding.edtUserEmailAddress.text.toString().trim())
                ?.addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        updateOtherDetails()
                    }
                }
                ?.addOnFailureListener {
                    dialog.dismiss()
                    showToast(it.localizedMessage)
                }
        }
    }

    private fun updateOtherDetails() {
        userData.child("${firebaseAuth.currentUser?.uid}/${FirebaseConstants.DATA_FULL_NAME}").setValue(binding.edtUserFullName.text.toString().trim())
        userData.child("${firebaseAuth.currentUser?.uid}/${FirebaseConstants.DATA_EMAIL_ADDRESS}").setValue(binding.edtUserEmailAddress.text.toString().trim())
        userData.child("${firebaseAuth.currentUser?.uid}/${FirebaseConstants.DATA_PHONE_NUMBER}").setValue(binding.edtUserPhoneNumber.text.toString().trim())
            .addOnSuccessListener {
                dialog.dismiss()
                showToast("Profile Updated")
            }
            .addOnFailureListener {
                dialog.dismiss()
                showToast(it.localizedMessage)
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
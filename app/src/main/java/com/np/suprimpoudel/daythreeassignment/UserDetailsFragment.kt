package com.np.suprimpoudel.daythreeassignment

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.np.suprimpoudel.daythreeassignment.databinding.FragmentUserDetailsBinding
import com.np.suprimpoudel.daythreeassignment.features.fragment.SignUpFragment
import com.np.suprimpoudel.daythreeassignment.features.shared.model.User
import com.np.suprimpoudel.daythreeassignment.network.FirebaseService
import com.np.suprimpoudel.daythreeassignment.utils.FirebaseConstants
import java.io.ByteArrayOutputStream

class UserDetailsFragment : Fragment() {

    private lateinit var binding: FragmentUserDetailsBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userData: DatabaseReference
    private lateinit var user: User
    private lateinit var dialog: Dialog
    private lateinit var firebaseStorage: FirebaseStorage

    private var profilePhotoURI: Uri? = null

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
        userData.child(firebaseAuth.currentUser?.uid!!).addListenerForSingleValueEvent(object :
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
        binding.imgUserProfilePicture.setOnClickListener {
            pickImageFromGallery()
        }
        binding.txtUpdateProfilePictureBtn.setOnClickListener {
            uploadImageToBucket()
        }
    }

    private fun updateProfile() {
        val email = binding.edtUserEmailAddress.text.toString()
        val phoneNumber = binding.edtUserPhoneNumber.text.toString()
        val fullName = binding.edtUserFullName.text.toString()

        if (email.isNotEmpty() && phoneNumber.isNotEmpty() && fullName.isNotEmpty()) {
            dialog.show()
            firebaseAuth.currentUser?.updateEmail(
                binding.edtUserEmailAddress.text.toString().trim()
            )
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
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
        userData.child("${firebaseAuth.currentUser?.uid}/${FirebaseConstants.DATA_FULL_NAME}")
            .setValue(binding.edtUserFullName.text.toString().trim())
        userData.child("${firebaseAuth.currentUser?.uid}/${FirebaseConstants.DATA_EMAIL_ADDRESS}")
            .setValue(binding.edtUserEmailAddress.text.toString().trim())
        userData.child("${firebaseAuth.currentUser?.uid}/${FirebaseConstants.DATA_PHONE_NUMBER}")
            .setValue(binding.edtUserPhoneNumber.text.toString().trim())
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

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, SignUpFragment.IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SignUpFragment.IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            binding.imgUserProfilePicture.setImageURI(data?.data)
            profilePhotoURI = data?.data

            binding.txtUpdateProfilePictureBtn.visibility = View.VISIBLE
        }
    }

    private fun uploadImageToBucket() {
        dialog.show()
        firebaseStorage = FirebaseService.getFirebaseStorage()
        val uid = firebaseAuth.currentUser?.uid ?: ""

        val reference =
            firebaseStorage.getReference(FirebaseConstants.DATA_PROFILE_PICTURE).child(uid)

        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                activity?.application?.contentResolver,
                profilePhotoURI
            )
        } catch (e: Exception) {
            showToast(e.localizedMessage)
        }
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 20, baos)
        val bitmapData = baos.toByteArray()
        reference.putBytes(bitmapData)
            .addOnFailureListener { e ->
                dialog.dismiss()
                showToast(e.localizedMessage)
            }
            .addOnSuccessListener { _ ->
                reference.downloadUrl
                    .addOnSuccessListener { url ->
                        dialog.dismiss()
                        userData.child("${firebaseAuth.currentUser?.uid}/${FirebaseConstants.DATA_PROFILE_PICTURE}")
                            .setValue(url.toString())
                        binding.txtUpdateProfilePictureBtn.visibility = View.GONE
                    }
                    .addOnFailureListener {
                        dialog.dismiss()
                        showToast(it.localizedMessage)
                    }
            }
    }

}
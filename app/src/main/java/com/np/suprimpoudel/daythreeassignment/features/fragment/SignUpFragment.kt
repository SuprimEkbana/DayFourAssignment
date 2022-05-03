package com.np.suprimpoudel.daythreeassignment.features.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.np.suprimpoudel.daythreeassignment.R
import com.np.suprimpoudel.daythreeassignment.databinding.FragmentSignUpBinding
import com.np.suprimpoudel.daythreeassignment.features.shared.model.User
import com.np.suprimpoudel.daythreeassignment.network.FirebaseService
import com.np.suprimpoudel.daythreeassignment.utils.FirebaseConstants.Companion.DATA_PROFILE_PICTURE
import com.np.suprimpoudel.daythreeassignment.utils.FirebaseConstants.Companion.DATA_USERS
import java.io.ByteArrayOutputStream

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var dialog: Dialog
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var firebaseDatabase: FirebaseDatabase

    private var profilePhotoURI: Uri? = null

    companion object {
        const val IMAGE_REQUEST_CODE = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignUpBinding.bind(view)

        initFirebaseService()
        initListener()
        initDialog()
    }

    private fun initFirebaseService() {
        firebaseAuth = FirebaseService.getFirebaseAuth()
    }

    private fun initListener() {
        binding.imgBackBtn.setOnClickListener {
            popBack()
        }
        binding.txtLoginBtn.setOnClickListener {
            popBack()
        }
        binding.btnSignUp.setOnClickListener { v ->
            signUpUser(v)
        }
        binding.imgProfilePicture.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private fun initDialog() {
        this.dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog)
        dialog.setCancelable(false)
    }

    private fun popBack() {
        findNavController().popBackStack()
    }

    private fun signUpUser(v: View) {
        val email = binding.edtEmailAddressSignUp.text.toString().trim()
        val password = binding.edtPasswordSignUp.text.toString().trim()

        if (profilePhotoURI != null) {
            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(v, "Please input all fields", Snackbar.LENGTH_SHORT).show()
            } else {
                dialog.show()
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            uploadImageToBucket(v)
                        } else {
                            dialog.dismiss()
                            showSnackBar(task.exception?.localizedMessage!!, v)
                            Log.d("Error", task.exception?.localizedMessage!!)
                        }
                    }
                    .addOnFailureListener {
                        dialog.dismiss()
                        showSnackBar(it.localizedMessage, v)
                        Log.d("Error", it.localizedMessage)
                    }
            }
        } else {
            showSnackBar("Please select a photo", v)
        }
    }

    private fun uploadImageToBucket(v: View) {
        firebaseStorage = FirebaseService.getFirebaseStorage()
        val uid = firebaseAuth.currentUser?.uid ?: ""

        val reference = firebaseStorage.getReference(DATA_PROFILE_PICTURE).child(uid)

        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                activity?.application?.contentResolver,
                profilePhotoURI
            )
        } catch (e: Exception) {
            showSnackBar(e.localizedMessage, v)
        }
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 20, baos)
        val bitmapData = baos.toByteArray()
        reference.putBytes(bitmapData)
            .addOnFailureListener { e ->
                dialog.dismiss()
                showSnackBar(e.localizedMessage, v)
            }
            .addOnSuccessListener { _ ->
                reference.downloadUrl
                    .addOnSuccessListener { url ->
                        storeDataFirebase(url, uid, v)
                    }
                    .addOnFailureListener {
                        dialog.dismiss()
                        showSnackBar(it.localizedMessage, v)
                    }
            }
    }

    private fun storeDataFirebase(url: Uri?, uid: String, v: View) {

        val u = User(
            uid,
            url.toString()
        )

        firebaseDatabase = FirebaseDatabase.getInstance()

        firebaseDatabase
            .getReference(DATA_USERS)
            .child(uid)
            .setValue(u)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dialog.dismiss()
                    showSnackBar("Sign Up Successfull", v)
                    findNavController().popBackStack()
                }
            }
            .addOnFailureListener {
                dialog.dismiss()
                showSnackBar(it.localizedMessage, v)
            }
    }

    private fun showSnackBar(message: String, v: View) {
        Snackbar.make(v, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            binding.imgProfilePicture.setImageURI(data?.data)
            profilePhotoURI = data?.data
        }
    }
}
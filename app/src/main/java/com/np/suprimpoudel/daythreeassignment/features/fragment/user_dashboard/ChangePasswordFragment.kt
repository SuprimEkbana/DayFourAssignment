package com.np.suprimpoudel.daythreeassignment.features.fragment.user_dashboard

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.np.suprimpoudel.daythreeassignment.R
import com.np.suprimpoudel.daythreeassignment.databinding.FragmentChangePasswordBinding
import com.np.suprimpoudel.daythreeassignment.network.FirebaseService

class ChangePasswordFragment : Fragment() {

    private lateinit var binding: FragmentChangePasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChangePasswordBinding.bind(view)

        initListener()
        initDialog()
    }

    private fun initDialog() {
        this.dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog)
        dialog.setCancelable(false)
    }

    private fun initListener() {
        binding.imgBackBtn.setOnClickListener {
            popBack()
        }
        binding.btnUpdatePassword.setOnClickListener {
            updatePassword()
        }
    }

    private fun updatePassword() {
        val currentPassword = binding.edtCurrentPassword.text.toString().trim()
        val newPassword = binding.edtNewPassword.text.toString().trim()
        val retypePassword = binding.edtRetypePassword.text.toString().trim()

        if (currentPassword.isEmpty() || newPassword.isEmpty() || retypePassword.isEmpty()) {
            showMessage("Please enter all fields")
        } else if (newPassword != retypePassword) {
            binding.tilRetypePassword.error = "New password and retype password doesn't match"
        } else {
            dialog.show()
            binding.tilRetypePassword.error = ""
            binding.tilRetypePassword.isErrorEnabled = false

            firebaseAuth = FirebaseService.getFirebaseAuth()

            firebaseAuth.currentUser?.updatePassword(newPassword)
                ?.addOnCompleteListener { task ->
                    dialog.dismiss()
                    if (task.isSuccessful) {
                        showMessage("Password Updated Successfully")
                        popBack()
                    }
                }
                ?.addOnFailureListener {
                    dialog.dismiss()
                    it.localizedMessage?.let { it1 -> showMessage(it1) }
                }
        }
    }

    private fun popBack() {
        findNavController().popBackStack()
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
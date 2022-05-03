package com.np.suprimpoudel.daythreeassignment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.np.suprimpoudel.daythreeassignment.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var dialog: Dialog

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignUpBinding.bind(view)

        initListener()
        initDialog()
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

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(v, "Please input all fields", Snackbar.LENGTH_SHORT).show()
        } else {
            dialog.show()
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        dialog.dismiss()
                        showSnackBar("Created account successfully", v)
                        popBack()
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
    }

    private fun showSnackBar(message: String, v: View) {
        Snackbar.make(v, message, Snackbar.LENGTH_SHORT).show()
    }
}
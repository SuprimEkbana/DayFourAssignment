package com.np.suprimpoudel.daythreeassignment.features.fragment.onboarding_helper

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.np.suprimpoudel.daythreeassignment.R
import com.np.suprimpoudel.daythreeassignment.databinding.FragmentLoginBinding
import com.np.suprimpoudel.daythreeassignment.features.activity.UserDashboard
import com.np.suprimpoudel.daythreeassignment.network.FirebaseService

class LoginFragment : Fragment() {

    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    private lateinit var dialog: Dialog
    private lateinit var binding: FragmentLoginBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)


        checkIfLoggedIn()
        initFirebaseService()
        initListener()
        initDialog()
    }

    private fun checkIfLoggedIn() {
        if(user != null) {
            redirectToUserDashboard()
        }
    }

    private fun initFirebaseService() {
        firebaseAuth = FirebaseService.getFirebaseAuth()
    }

    private fun initListener() {
        binding.btnLogin.setOnClickListener { v ->
            loginInIntoAccount(v)
        }
        binding.txtSignUpBtn.setOnClickListener {
            redirectToSignUpScreen()
        }
    }

    private fun initDialog() {
        this.dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog)
        dialog.setCancelable(false)
    }

    private fun loginInIntoAccount(v: View) {
        val email = binding.edtEmailAddress.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(v, "Please input all fields", Snackbar.LENGTH_SHORT).show()
        } else {
            dialog.show()
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        dialog.dismiss()
                        redirectToUserDashboard()
                    } else {
                        dialog.dismiss()
                        showSnackBar(task.exception?.localizedMessage!!, v)
                        Log.d("Error", task.exception?.localizedMessage!!)

                    }
                }
                .addOnFailureListener {
                    dialog.dismiss()
                    it.localizedMessage?.let { it1 -> showSnackBar(it1, v) }
                }
        }
    }

    private fun showSnackBar(message: String, v: View) {
        Snackbar.make(v, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun redirectToSignUpScreen() {
        findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
    }

    private fun redirectToUserDashboard() {
        startActivity(Intent(context, UserDashboard::class.java))
        activity?.finish()
    }
}
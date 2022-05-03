package com.np.suprimpoudel.daythreeassignment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.np.suprimpoudel.daythreeassignment.databinding.FragmentSettingBinding
import com.np.suprimpoudel.daythreeassignment.features.activity.MainActivity
import com.np.suprimpoudel.daythreeassignment.network.FirebaseService

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSettingBinding.bind(view)

        initListener()
    }

    private fun initListener() {
        binding.cardChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_changePasswordFragment)
        }
        binding.cardLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        firebaseAuth = FirebaseService.getFirebaseAuth()
        firebaseAuth.signOut()

        startActivity(Intent(context, MainActivity::class.java))
        activity?.finish()
    }
}
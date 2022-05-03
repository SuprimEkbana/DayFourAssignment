package com.np.suprimpoudel.daythreeassignment.features.activity

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.np.suprimpoudel.daythreeassignment.R
import com.np.suprimpoudel.daythreeassignment.databinding.ActivityUserDashboardBinding

class UserDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityUserDashboardBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpNavigationController()
        setUpBottomNavigationWithNavController()
    }

    private fun setUpNavigationController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerUserDashboard) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setUpBottomNavigationWithNavController() {
        NavigationUI.setupWithNavController(binding.bottomNavUserDashboard, navController)
    }

}
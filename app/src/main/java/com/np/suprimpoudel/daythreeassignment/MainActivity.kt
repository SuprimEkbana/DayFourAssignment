package com.np.suprimpoudel.daythreeassignment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.np.suprimpoudel.daythreeassignment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Binding for Activity Main
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpNavigationController()

        setUpActionBarWithNavController()
    }

    private fun setUpNavigationController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerMainActivity) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun setUpActionBarWithNavController() {
        NavigationUI.setupActionBarWithNavController(this, navController)
        AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
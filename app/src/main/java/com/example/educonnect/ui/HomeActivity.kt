package com.example.educonnect.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.educonnect.LoginActivity
import com.example.educonnect.ui.home.viewmodel.DashboardViewModel
import com.example.educonnect.utils.toast
import com.example.educonnect_educationalapp.R
import com.example.educonnect_educationalapp.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private val dashboardViewModel: DashboardViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_main) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(binding.toolbar)
        binding.navDrawer.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> navController.navigate(R.id.dashboardFragment)

            R.id.nav_setting -> navController.navigate(R.id.settingFragment)

            R.id.nav_profile -> navController.navigate(R.id.profileFragment)

            R.id.nav_share -> navController.navigate(R.id.shareFragment)

            R.id.nav_about_us -> navController.navigate(R.id.aboutUsFragment)

            R.id.nav_logout -> {
                AlertDialog.Builder(baseContext)
                    .setTitle(getString(R.string.do_you_want_to_signout))
                    .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                        dashboardViewModel.signOut()
                        startActivity(Intent(this, LoginActivity::class.java))
                        this.finish()
                    }
                    .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
package com.example.face

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Toast

class Afterlogin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_afterlogin)

        // Retrieve all the extras sent from the LoginActivity
        val extras = intent.extras
        if (extras == null) {
            Toast.makeText(this, "Error: No data received!", Toast.LENGTH_SHORT).show()
            return
        }

        // Pass all extras to the HomeFragment and SettingsFragment
        val homeFragment = HomeFragment().apply {
            arguments = extras
        }
        val settingsFragment = SettingsFragment().apply {
            arguments = extras
        }

        // Load the default fragment
        loadFragment(homeFragment)

        // Set up the bottom navigation view
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    loadFragment(homeFragment)
                    true
                }
                R.id.settingsFragment -> {
                    loadFragment(settingsFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()

        // Add logging to verify fragment transaction
        Toast.makeText(this, "Loaded ${fragment::class.java.simpleName}", Toast.LENGTH_SHORT).show()
    }
}

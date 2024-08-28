package com.example.face


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val registerButton = findViewById<Button>(R.id.register_button)

        registerButton.setOnClickListener {
            Log.d("MainActivity", "Register button clicked")
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        val btnLoginMain: Button = findViewById(R.id.login_button)

        btnLoginMain.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}

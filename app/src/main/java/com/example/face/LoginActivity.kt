package com.example.face


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var googleSheetsHelper: GoogleSheetsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val webAppUrl = "https://script.google.com/macros/s/AKfycbzP9_MMshiu_PO4scr3C23-SuSZZ2aJzJd4LmD3CATLyLfhIThJGUcChGrRu6G3H37SQg/exec"
        googleSheetsHelper = GoogleSheetsHelper(webAppUrl)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and Password must not be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            googleSheetsHelper.verifyLogin(email, password) { success, data ->
                runOnUiThread {
                    if (success) {
                        val intent = Intent(this, Afterlogin::class.java)
                        data?.forEach { (key, value) ->
                            intent.putExtra(key, value.toString())
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Login failed. Please check your email and password.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

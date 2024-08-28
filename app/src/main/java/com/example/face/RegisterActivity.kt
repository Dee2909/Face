package com.example.face

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var googleSheetsHelper: GoogleSheetsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val webAppUrl = "https://script.google.com/macros/s/AKfycbzP9_MMshiu_PO4scr3C23-SuSZZ2aJzJd4LmD3CATLyLfhIThJGUcChGrRu6G3H37SQg/exec"
        googleSheetsHelper = GoogleSheetsHelper(webAppUrl)

        val firstNameEditText = findViewById<EditText>(R.id.first_name)
        val lastNameEditText = findViewById<EditText>(R.id.last_name)
        val profileLinkEditText = findViewById<EditText>(R.id.profile_link)
        val authenticatorLinkEditText = findViewById<EditText>(R.id.authenticator_link)
        val unknownLinkEditText = findViewById<EditText>(R.id.unknown_link)
        val mobileNumberEditText = findViewById<EditText>(R.id.mobile_number)
        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirm_password)
        val submitButton = findViewById<Button>(R.id.submit_button)

        submitButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val profileLink = profileLinkEditText.text.toString()
            val authenticatorLink = authenticatorLinkEditText.text.toString()
            val unknownLink = unknownLinkEditText.text.toString()
            val mobileNumber = mobileNumberEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (firstName.isEmpty() || lastName.isEmpty() ||profileLink.isEmpty()|| authenticatorLink.isEmpty() || unknownLink.isEmpty() || mobileNumber.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                val values = mapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "profileLink" to profileLink,
                    "authorLink" to authenticatorLink,
                    "unknownLink" to unknownLink,
                    "mobileNumber" to mobileNumber,
                    "email" to email,
                    "password" to password
                )

                googleSheetsHelper.appendRow(values) { success ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to register. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}

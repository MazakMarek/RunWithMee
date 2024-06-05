package com.example.yourapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.runwithme.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class PasswordChangeActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.password_change_page)

        val newPassword = findViewById<EditText>(R.id.newPassword)
        val confirmNewPassword = findViewById<EditText>(R.id.confirmNewPassword)
        val changePasswordButton = findViewById<Button>(R.id.changePasswordButton)
        val user = Firebase.auth.currentUser

        changePasswordButton.setOnClickListener {
            val newPassword = newPassword.text.toString()
            val confirmNewPassword = confirmNewPassword.text.toString()

            if (newPassword == confirmNewPassword) {
                user!!.updatePassword(newPassword)
                Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
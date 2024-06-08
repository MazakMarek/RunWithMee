package com.example.runwithme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.runwithme.databinding.ProfilePageBinding
import com.example.yourapp.PasswordChangeActivity

class ProfilePage : ComponentActivity() {

    private lateinit var binding: ProfilePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val changePasswordTextView = findViewById<TextView>(R.id.textViewChangePassword)
        val backButton = findViewById<TextView>(R.id.ButtonBack)

        changePasswordTextView.setOnClickListener {
            val intent = Intent(this, PasswordChangeActivity::class.java)
            Log.d("ProfilePage", "Changing password")
            startActivity(intent)
        }
        backButton.setOnClickListener {
            val intent = Intent(this, MainPage::class.java)
            startActivity(intent)
        }
    }
}
package com.example.runwithme

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.runwithme.databinding.LoginPageBinding
import com.example.runwithme.databinding.MainPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainPage : ComponentActivity() {

    private lateinit var binding: MainPageBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val profileImage = binding.profileImage
        val startButton = binding.startActivityButton

        profileImage.setOnClickListener {
            val intent = Intent(this, ProfilePage::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            }
            startActivity(intent)
        }

        startButton.setOnClickListener {
            val intent = Intent(this, StartActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            }
            startActivity(intent)
        }
    }

    @Composable
    private fun MainScreen() {
        val currentUser = firebaseAuth.currentUser
        val currentUserID = currentUser?.uid

        val nickname = remember { mutableStateOf("") }

        LaunchedEffect(currentUserID) {
            if (currentUserID != null) {
                usersRef.child(currentUserID).get().addOnSuccessListener { dataSnapshot ->
                    val retrievedNickname = dataSnapshot.child("nickname").getValue(String::class.java)
                    nickname.value = retrievedNickname ?: "No nickname"
                }.addOnFailureListener {
                    // Handle database error
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = nickname.value)
        }
    }
}
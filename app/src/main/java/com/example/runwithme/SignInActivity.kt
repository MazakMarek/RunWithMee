package com.example.runwithme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.runwithme.databinding.SignInPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : ComponentActivity() {

    private lateinit var binding: SignInPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignInPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseReference = FirebaseDatabase.getInstance().reference

        firebaseAuth = FirebaseAuth.getInstance()

        binding.alreadyAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.SignInButton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            val nickname = binding.nickname.text.toString()

            if (nickname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                if (nickname.isEmpty()) {
                    binding.nickname.error = "Please enter a nickname"
                }
                if (email.isEmpty()) {
                    binding.email.error = "Please enter email"
                }
                if (password.isEmpty()) {
                    binding.password.error = "Please enter password"
                }
                if (confirmPassword.isEmpty()) {
                    binding.confirmPassword.error = "Please enter confirmation password"
                }
            } else {
                if (password == confirmPassword)
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userID = task.result!!.user!!.uid
                                val user = UserModel(userID, nickname, email)
                                databaseReference.child("testPath").setValue("testValue")
                                    .addOnSuccessListener {
                                        Log.d("SignInActivity", "Successfully wrote to testPath")
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("SignInActivity", "Failed to write to testPath", exception)
                                    }
                                databaseReference.child(userID).setValue(user)
                                    .addOnCompleteListener { innerTask ->


                                        if (innerTask.isSuccessful) {
                                            binding.email.text.clear()
                                            binding.password.text.clear()
                                            binding.confirmPassword.text.clear()
                                            startActivity(Intent(this, LoginActivity::class.java))
                                            finish()
                                            Toast.makeText(
                                                this,
                                                "zapis do firebase by mal nastat",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        } else {
                                            // Log the error and show a Toast message
                                            Log.e("SignInActivity", "Failed to write to database", innerTask.exception)
                                            Toast.makeText(this, "Failed to write to database: ${innerTask.exception?.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        if (exception != null) {
                                            binding.confirmPassword.error = exception.message
                                            Toast.makeText(this, "zapis do firebase by mal nastat", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    }
                            } else {
                                // Log the error and show a Toast message
                                Log.e("SignInActivity", "Failed to create user", task.exception)
                                Toast.makeText(this, "Failed to create user: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            if (exception != null) {
                                binding.confirmPassword.error = exception.message
                                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                else {
                    binding.confirmPassword.error = "Password does not match asdasd"
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT)
                        .show()
                }

            }
            }
        }
    }


package com.example.runwithme

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.runwithme.databinding.SignInPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : ComponentActivity() {

    private lateinit var binding: SignInPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignInPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.alreadyAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.SignInButton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()


            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
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
                if (password.equals(confirmPassword))
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userID = task.result!!.user!!.uid
                                val nickname = binding.nickname.text.toString()

                                val userRef =
                                    FirebaseDatabase.getInstance().getReference("users/$userID")
                                val user =
                                    hashMapOf<String, Any>("nickname" to nickname, "email" to email)
                                userRef.setValue(user).addOnCompleteListener { innerTask ->


                                    if (innerTask.isSuccessful) {
                                        binding.email.text.clear()
                                        binding.password.text.clear()
                                        binding.confirmPassword.text.clear()
                                        startActivity(Intent(this, LoginActivity::class.java))
                                        finish()
                                    } else {
                                        binding.confirmPassword.error =
                                            innerTask.exception.toString()
                                    }
                                }
                            } else {
                                binding.confirmPassword.error = "Password does not match"
                            }

                        }
            }
        }
    }
}
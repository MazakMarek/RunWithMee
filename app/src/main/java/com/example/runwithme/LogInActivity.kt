package com.example.runwithme

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.runwithme.databinding.LoginPageBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {

    private lateinit var binding: LoginPageBinding
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.withoutAccount.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.loginButton.setOnClickListener{
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                if (email.isEmpty()) {
                    binding.email.error = "Please enter email"
                }
                else {
                    binding.password.error = "Please enter password"
                }

            }else {
               firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                   if (it.isSuccessful) {
                       startActivity(Intent(this, MainPage::class.java))
                   }else {
                       binding.password.error = "Incorrect password"
                   }
               }

            }
        }
    }
}
package com.example.runwithme

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.runwithme.databinding.ActivityPageBinding

class ActivityPage : ComponentActivity() {

    private lateinit var binding: ActivityPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}

class StartActivity {
}
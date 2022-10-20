package com.rookmotion.rooktraining.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rookmotion.rooktraining.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
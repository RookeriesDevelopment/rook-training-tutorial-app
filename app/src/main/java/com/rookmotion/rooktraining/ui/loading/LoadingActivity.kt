package com.rookmotion.rooktraining.ui.loading

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rookmotion.rooktraining.databinding.ActivityLoadingBinding
import com.rookmotion.rooktraining.ui.auth.AuthActivity

class LoadingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startActivity(Intent(this, AuthActivity::class.java))
    }
}
package com.rookmotion.rooktraining.ui.loading

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.rookmotion.rooktraining.databinding.ActivityLoadingBinding
import com.rookmotion.rooktraining.state.AuthViewModel
import com.rookmotion.rooktraining.state.RMViewModelFactory
import com.rookmotion.rooktraining.ui.auth.AuthActivity
import com.rookmotion.rooktraining.ui.home.HomeActivity
import com.rookmotion.rooktraining.utils.rmLocator
import kotlinx.coroutines.delay

class LoadingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoadingBinding

    private val authViewModel by viewModels<AuthViewModel> { RMViewModelFactory(rmLocator) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel.isAuthenticated.observe(this) {
            if (it) {
                finish()
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                finish()
                startActivity(Intent(this, AuthActivity::class.java))
            }
        }

        lifecycleScope.launchWhenResumed {
            delay(3000)
            authViewModel.checkUserAuthentication()
        }
    }
}
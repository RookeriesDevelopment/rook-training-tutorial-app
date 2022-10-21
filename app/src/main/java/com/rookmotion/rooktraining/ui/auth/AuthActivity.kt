package com.rookmotion.rooktraining.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rookmotion.rooktraining.databinding.ActivityAuthBinding
import com.rookmotion.rooktraining.state.AuthViewModel
import com.rookmotion.rooktraining.state.RMViewModelFactory
import com.rookmotion.rooktraining.ui.home.HomeActivity
import com.rookmotion.rooktraining.utils.rmLocator
import com.rookmotion.rooktraining.utils.toastLong

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private val authViewModel by viewModels<AuthViewModel> { RMViewModelFactory(rmLocator) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel.loginState.observe(this) {
            when (it) {
                is AuthViewModel.LoginState.Logged -> {
                    finish()
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                is AuthViewModel.LoginState.Error -> {
                    toastLong(it.message)
                    authViewModel.resetLoginState()
                }
                AuthViewModel.LoginState.Loading -> binding.login.isEnabled = false
                AuthViewModel.LoginState.None -> binding.login.isEnabled = true
            }
        }

        binding.login.setOnClickListener {
            authViewModel.login(binding.email.text.toString())
        }
    }
}
package com.rookmotion.rooktraining.ui.home

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rookmotion.rooktraining.databinding.ActivityHomeBinding
import com.rookmotion.rooktraining.state.RMViewModelFactory
import com.rookmotion.rooktraining.state.UserViewModel
import com.rookmotion.rooktraining.utils.rmLocator

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val userViewModel by viewModels<UserViewModel> { RMViewModelFactory(rmLocator) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel.user.observe(this) {
            if (it != null) {
                binding.userUuid.text = it.userUUID
                binding.userEmail.text = it.email
            } else {
                binding.userUuid.text = "N/A"
                binding.userEmail.text = "N/A"
            }
        }

        userViewModel.getUser()
        userViewModel.syncIndexes()
    }
}
package com.rookmotion.rooktraining.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rookmotion.rooktraining.databinding.ActivityHomeBinding
import com.rookmotion.rooktraining.state.RMViewModelFactory
import com.rookmotion.rooktraining.state.UserViewModel
import com.rookmotion.rooktraining.ui.trainingtype.TrainingTypeActivity
import com.rookmotion.rooktraining.utils.rmLocator
import timber.log.Timber

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val userViewModel by viewModels<UserViewModel> { RMViewModelFactory(rmLocator) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initPermissions()
        initState()
        initActions()

        userViewModel.getUser()
        userViewModel.syncIndexes()
    }

    private fun initPermissions() {

    }

    private fun initState() {
        userViewModel.user.observe(this) {
            if (it != null) {
                binding.userUuid.text = "UUID: ${it.userUUID}"
                binding.userEmail.text = "Email: ${it.email}"
            } else {
                Timber.e("user error: user state returned null")
            }
        }
    }

    private fun initActions() {
        binding.individualTraining.setOnClickListener {
            startActivity(Intent(this, TrainingTypeActivity::class.java))
        }
    }
}
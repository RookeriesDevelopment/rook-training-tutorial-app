package com.rookmotion.rooktraining.ui.trainingtype

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.rookmotion.app.sdk.persistence.entities.training.RMTrainingType
import com.rookmotion.rooktraining.R
import com.rookmotion.rooktraining.databinding.ActivityTrainingTypeBinding
import com.rookmotion.rooktraining.state.RMViewModelFactory
import com.rookmotion.rooktraining.state.TrainingTypeViewModel
import com.rookmotion.rooktraining.ui.trainingtype.adapter.TrainingTypeAdapter
import com.rookmotion.rooktraining.utils.rmLocator
import com.rookmotion.rooktraining.utils.toastShort

class TrainingTypeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrainingTypeBinding

    private val trainingTypeViewModel by viewModels<TrainingTypeViewModel> {
        RMViewModelFactory(rmLocator)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        trainingTypeViewModel.trainingTypeState.observe(this) {
            when (it) {
                is TrainingTypeViewModel.TrainingTypeState.DataAvailable -> showItems(it.data)
                TrainingTypeViewModel.TrainingTypeState.NoDataAvailable -> showNoItems()
                is TrainingTypeViewModel.TrainingTypeState.Error -> showError(it.message)
                TrainingTypeViewModel.TrainingTypeState.Loading -> showProgress()
            }
        }

        binding.error.errorRetry.setOnClickListener { trainingTypeViewModel.getTrainingTypes() }
    }

    private fun goToTraining(trainingType: RMTrainingType) {
        toastShort("WIP")
    }

    private fun showProgress() {
        binding.progress.root.isVisible = true
        binding.error.root.isVisible = false
        binding.data.isVisible = false
    }

    private fun showItems(items: List<RMTrainingType>) {
        binding.data.adapter = TrainingTypeAdapter(items) { goToTraining(it) }

        binding.progress.root.isVisible = false
        binding.error.root.isVisible = false
        binding.data.isVisible = true
    }

    private fun showNoItems() {
        binding.error.errorMessage.text = getString(R.string.no_trainings_types_available)

        binding.progress.root.isVisible = false
        binding.error.root.isVisible = true
        binding.data.isVisible = false
    }

    private fun showError(error: String) {
        binding.error.errorMessage.text = error

        binding.progress.root.isVisible = false
        binding.error.root.isVisible = true
        binding.data.isVisible = false
    }
}
package com.rookmotion.rooktraining.ui.training.summary

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.rookmotion.app.sdk.persistence.entities.training.HeartRateDerivedData
import com.rookmotion.app.sdk.persistence.entities.training.RMSummary
import com.rookmotion.app.sdk.persistence.entities.training.RMTrainingModel
import com.rookmotion.app.sdk.persistence.entities.training.StepsDerivedData
import com.rookmotion.kotlin.sdk.domain.enums.StepsType
import com.rookmotion.rooktraining.R
import com.rookmotion.rooktraining.databinding.ActivityTrainingSummaryBinding
import com.rookmotion.rooktraining.state.RMViewModelFactory
import com.rookmotion.rooktraining.state.TrainingSummaryViewModel
import com.rookmotion.rooktraining.ui.training.TrainingSummaryKeys
import com.rookmotion.rooktraining.utils.rmLocator
import com.rookmotion.rooktraining.utils.snackLong
import com.rookmotion.utils.sdk.format.TimeFormatter
import com.rookmotion.utils.sdk.time.UTCConverter
import timber.log.Timber
import kotlin.math.roundToInt

class TrainingSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrainingSummaryBinding

    private val trainingSummaryViewModel by viewModels<TrainingSummaryViewModel> {
        RMViewModelFactory(rmLocator)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initExtras()
        initState()
    }

    private fun initExtras() {
        val start = intent.extras?.getString(TrainingSummaryKeys.START) ?: ""
        val trainingType = intent.extras?.getString(TrainingSummaryKeys.TRAINING_TYPE) ?: ""
        val stepsType = StepsType.valueOf(
            intent.getStringExtra(TrainingSummaryKeys.STEPS_TYPE) ?: "NONE"
        )

        binding.trainingType.text = trainingType

        if (stepsType != StepsType.NONE) {
            binding.stepsTypes.text = stepsType.name

            binding.stepsTypes.isVisible = true
            binding.steps.isVisible = true
        } else {
            binding.stepsTypes.isVisible = false
            binding.steps.isVisible = false
        }

        trainingSummaryViewModel.getTraining(start)
    }

    private fun initState() {
        trainingSummaryViewModel.trainingSummaryState.observe(this) {
            when (it) {
                TrainingSummaryViewModel.TrainingSummaryState.None -> Timber.i("trainingSummaryState.None")
                TrainingSummaryViewModel.TrainingSummaryState.Loading -> {
                    binding.progress.root.isVisible = true
                }
                is TrainingSummaryViewModel.TrainingSummaryState.Success -> {

                    showTrainingInfo(it.training)
                    showSummaryInfo(it.summary)
                    showRecordsInfo(it.heartRateRecords, it.stepsRecords)

                    binding.progress.root.isVisible = false
                }
                is TrainingSummaryViewModel.TrainingSummaryState.Error -> {
                    binding.root.snackLong(it.message, getString(R.string.retry)) {
                        trainingSummaryViewModel.getTraining(
                            start = intent.extras?.getString(TrainingSummaryKeys.START) ?: ""
                        )
                    }

                    binding.progress.root.isVisible = false
                }
            }
        }
    }

    private fun showTrainingInfo(training: RMTrainingModel) {
        binding.trainingName.text =
            if (training.deviceType == "app") getString(R.string.individual_training)
            else getString(R.string.remote_training)

        binding.startDate.text = UTCConverter.getDateTime(training.start, true)?.toString()
        binding.endDate.text = UTCConverter.getDateTime(training.end, true)?.toString()
    }

    private fun showSummaryInfo(summary: RMSummary) {
        binding.duration.text = TimeFormatter.formatSecondsToHHMMSS(summary.totalTime)
        binding.steps.text = summary.totalSteps.toString()
        binding.calories.text = summary.totalCalories.roundToInt().toString()
        binding.effort.text = getString(
            R.string.effort_min_avg_max_placeholder,
            summary.minEffort.roundToInt().toString(),
            summary.avgEffort.roundToInt().toString(),
            summary.maxEffort.roundToInt().toString()
        )
        binding.heartRate.text = getString(
            R.string.hr_min_avg_max_placeholder,
            summary.minHeartRate.roundToInt().toString(),
            summary.avgHeartRate.roundToInt().toString(),
            summary.maxHeartRate.roundToInt().toString()
        )
    }

    private fun showRecordsInfo(
        heartRateRecords: List<HeartRateDerivedData>,
        stepsRecords: List<StepsDerivedData>
    ) {
        binding.heartRateRecords.text = heartRateRecords.size.toString()
        binding.stepsRecords.text = stepsRecords.size.toString()
    }
}
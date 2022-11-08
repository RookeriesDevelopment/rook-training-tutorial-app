package com.rookmotion.rooktraining.ui.training.solo

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.rookmotion.app.sdk.persistence.entities.training.HeartRateDerivedData
import com.rookmotion.app.sdk.rmsensor.SensorConnectionState
import com.rookmotion.kotlin.sdk.data.mapper.TrainingTypeMapper
import com.rookmotion.rooktraining.R
import com.rookmotion.rooktraining.RookTrainingApplication
import com.rookmotion.rooktraining.databinding.ActivityTrainingSoloBinding
import com.rookmotion.rooktraining.state.RMApplicationViewModelFactory
import com.rookmotion.rooktraining.state.TrainingSoloViewModel
import com.rookmotion.rooktraining.ui.scanner.ScannerKeys
import com.rookmotion.rooktraining.ui.scanner.SensorScannerActivity
import com.rookmotion.rooktraining.ui.training.SessionType
import com.rookmotion.rooktraining.ui.training.TrainingKeys
import com.rookmotion.rooktraining.ui.training.TrainingSummaryKeys
import com.rookmotion.rooktraining.ui.training.summary.TrainingSummaryActivity
import com.rookmotion.rooktraining.utils.AppResources
import com.rookmotion.rooktraining.utils.Dialogs
import com.rookmotion.rooktraining.utils.setStatusBarColor
import com.rookmotion.rooktraining.utils.toastShort
import com.rookmotion.utils.sdk.format.TimeFormatter
import com.rookmotion.utils.sdk.permissions.BatteryManager
import com.welie.blessed.BluetoothPeripheral
import timber.log.Timber
import kotlin.math.roundToInt

class TrainingSoloActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrainingSoloBinding
    private lateinit var sensorsLauncher: ActivityResultLauncher<Intent>

    private val batteryManager = BatteryManager()

    private val trainingSoloViewModel by viewModels<TrainingSoloViewModel> {
        RMApplicationViewModelFactory(application as RookTrainingApplication)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingSoloBinding.inflate(layoutInflater)
        setContentView(binding.root)

        batteryManager.registerIgnoreOptimizationsListener(
            componentActivity = this,
            onSuccess = { toastShort(getString(R.string.thanks)) },
            onFailure = { Timber.w("User rejected disabling battery optimizations") }
        )

        if (!batteryManager.isIgnoringBatteryOptimizations(this)) {
            batteryManager.requestIgnoreOptimizations(packageName)
        }

        initExtras()
        initSensorConnectionListeners()
        initTrainingListeners()
        initActions()
    }

    override fun onBackPressed() {
        if (trainingSoloViewModel.isTrainingStarted) {
            showStopTrainingDialog()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (!isChangingConfigurations) {
            trainingSoloViewModel.releaseTrainingResources()
        }

        super.onDestroy()
    }

    private fun initExtras() {
        val trainingTypeJson = intent.extras?.getString(TrainingKeys.TRAINING_TYPE_SELECTED) ?: ""

        if (trainingSoloViewModel.isTrainingNotStarted) {
            val trainingType = TrainingTypeMapper.fromJson(trainingTypeJson)

            if (trainingType != null) {
                trainingSoloViewModel.initTrainingType(trainingType)
            }
        } else {
            Timber.i("initExtras: training is active, skipped")
        }
    }

    private fun initSensorConnectionListeners() {
        lifecycleScope.launchWhenResumed {
            trainingSoloViewModel.trainer.connectionState.collect {
                when (it) {
                    is SensorConnectionState.Connecting -> {
                        val label = getString(
                            R.string.connecting_with_sensor,
                            it.sensor.name ?: getString(R.string.unknown_sensor)
                        )

                        binding.sensorState.setImageResource(R.drawable.ic_bluetooth_connecting)
                        binding.sensorName.text = label
                    }
                    is SensorConnectionState.ConnectionFailed -> {
                        binding.sensorState.setImageResource(R.drawable.ic_sensor_off)
                        binding.sensorName.setText(R.string.could_not_connect)
                    }
                    is SensorConnectionState.Connected -> {
                        binding.sensorState.setImageResource(R.drawable.ic_sensor)
                        binding.sensorName.text =
                            it.sensor.name ?: getString(R.string.unknown_sensor)
                    }
                    is SensorConnectionState.Disconnecting -> {
                        Timber.i("Disconnecting from sensor ${it.sensor.name}")
                    }
                    is SensorConnectionState.Disconnected -> {
                        binding.sensorState.setImageResource(R.drawable.ic_sensor_off)
                        binding.sensorName.setText(R.string.sensor_connection_lost)
                    }
                    SensorConnectionState.None -> Timber.i("SensorConnectionState.None")
                }
            }
        }

        sensorsLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK && it.data != null) {

                val mac = it.data?.getStringExtra(ScannerKeys.SENSOR_SELECTED)?.uppercase() ?: ""
                val success = trainingSoloViewModel.connect(mac)

                if (success) {
                    Timber.i("Connection request created successfully")
                } else {
                    Timber.i("Connection request not created")
                }
            }
        }
    }

    private fun initTrainingListeners() {
        lifecycleScope.launchWhenResumed {
            trainingSoloViewModel.sessionType.collect {
                when (it) {
                    SessionType.Unknown -> Timber.i("sessionType: None")
                    is SessionType.Classic -> {
                        binding.trainingTypeIcon.setImageResource(R.drawable.ic_training_type)
                        binding.trainingType.text = it.trainingType.trainingName
                        binding.stepsTypes.isVisible = false
                        binding.steps.isVisible = false
                    }
                    is SessionType.WithSteps -> {
                        binding.trainingTypeIcon.setImageResource(R.drawable.ic_training_type_steps)
                        binding.trainingType.text = it.trainingType.trainingName
                        binding.stepsTypes.isVisible = true
                        binding.steps.isVisible = true
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            trainingSoloViewModel.trainer.time.collect {
                binding.duration.text = TimeFormatter.formatSecondsToHHMMSS(it)
            }
        }

        lifecycleScope.launchWhenResumed {
            trainingSoloViewModel.trainer.heartRate.collect {
                if (it.calories > 0F) {
                    manageHeartRateData(it)
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            trainingSoloViewModel.trainer.hrValid.collect {
                Timber.i("Heart rate is grater than zero? $it")
            }
        }

        lifecycleScope.launchWhenResumed {
            trainingSoloViewModel.trainer.steps.collect {
                val label = AppResources.suffixOfStepsType(
                    context = this@TrainingSoloActivity,
                    stepsType = trainingSoloViewModel.getStepsType()
                )

                binding.steps.text = "$it $label"
            }
        }

        lifecycleScope.launchWhenResumed {
            trainingSoloViewModel.trainer.batteryLevel.collect {
                binding.batteryLevel.text = "$it%"
            }
        }

        lifecycleScope.launchWhenResumed {
            trainingSoloViewModel.trainer.bandContactState.collect {
                Timber.i("Band contact: ${it.name}")
            }
        }
    }

    private fun initActions() {
        binding.back.setOnClickListener { onBackPressed() }

        binding.chooseSensor.setOnClickListener { goToSelectSensor() }

        binding.play.setOnClickListener {
            if (trainingSoloViewModel.trainer.isTrainingPaused) {
                trainingSoloViewModel.resumeTraining()
            } else {
                trainingSoloViewModel.startTraining()
            }

            binding.play.isVisible = false
            binding.pause.isVisible = true
            binding.stop.isVisible = true
        }

        binding.pause.setOnClickListener {
            trainingSoloViewModel.pauseTraining()

            binding.play.isVisible = true
            binding.pause.isVisible = false
        }

        binding.stop.setOnClickListener { showStopTrainingDialog() }

        updateControlButtons()
    }

    private fun updateControlButtons() {
        if (trainingSoloViewModel.trainer.isTrainingActive) {
            binding.play.isVisible = false
            binding.pause.isVisible = true
        } else {
            binding.play.isVisible = true
            binding.pause.isVisible = false
        }

        if (trainingSoloViewModel.isTrainingStarted) {
            binding.stop.isVisible = true
        }
    }

    private fun manageHeartRateData(data: HeartRateDerivedData) {

        val effort = data.effort.roundToInt()

        binding.heartRate.text = "${data.heartRate.roundToInt()}"
        binding.calories.text = "${data.calories.roundToInt()}"
        binding.effort.text = "$effort"

        when {
            effort >= 90 -> {
                setStatusBarColor(R.color.zone_5)
                binding.base.setBackgroundColor(ContextCompat.getColor(this, R.color.zone_5))
            }
            effort >= 80 -> {
                setStatusBarColor(R.color.zone_4)
                binding.base.setBackgroundColor(ContextCompat.getColor(this, R.color.zone_4))
            }
            effort >= 70 -> {
                setStatusBarColor(R.color.zone_3)
                binding.base.setBackgroundColor(ContextCompat.getColor(this, R.color.zone_3))
            }
            effort >= 60 -> {
                setStatusBarColor(R.color.zone_2)
                binding.base.setBackgroundColor(ContextCompat.getColor(this, R.color.zone_2))
            }
            effort >= 50 -> {
                setStatusBarColor(R.color.zone_1)
                binding.base.setBackgroundColor(ContextCompat.getColor(this, R.color.zone_1))
            }
            else -> {
                setStatusBarColor(R.color.zone_0)
                binding.base.setBackgroundColor(ContextCompat.getColor(this, R.color.zone_0))
            }
        }
    }

    private fun goToSelectSensor() {
        val connected: BluetoothPeripheral? = trainingSoloViewModel.trainer.connectedSensor

        if (trainingSoloViewModel.trainer.connectionState.value is SensorConnectionState.Connecting) {
            trainingSoloViewModel.cancelConnection()
        }

        sensorsLauncher.launch(
            Intent(this, SensorScannerActivity::class.java).apply {
                putExtra(ScannerKeys.SENSOR_CONNECTED_MAC, connected?.address)
                putExtra(ScannerKeys.SENSOR_CONNECTED_NAME, connected?.name)
            }
        )
    }

    private fun showStopTrainingDialog() {
        Dialogs.showStopTrainingDialog(
            this,
            cancelTraining = {
                trainingSoloViewModel.cancelTraining()
                finish()
            },
            finishTraining = { finishTraining() }
        )
    }

    private fun finishTraining() {
        val startTime = trainingSoloViewModel.finishTraining()
        val trainingType = trainingSoloViewModel.trainer.trainingType
        val stepsType = trainingSoloViewModel.getStepsType()

        val intent = Intent(this, TrainingSummaryActivity::class.java).apply {
            putExtra(TrainingSummaryKeys.START, startTime)
            putExtra(TrainingSummaryKeys.TRAINING_TYPE, trainingType.trainingName)
            putExtra(TrainingSummaryKeys.STEPS_TYPE, stepsType.name)
        }

        startActivity(intent)
        finish()
    }
}
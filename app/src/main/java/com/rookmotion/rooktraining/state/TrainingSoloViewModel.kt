package com.rookmotion.rooktraining.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rookmotion.app.sdk.persistence.entities.training.RMTrainingType
import com.rookmotion.kotlin.sdk.domain.enums.StepsType
import com.rookmotion.rooktraining.ui.training.SessionType
import com.rookmotion.rooktraining.ui.training.TrainerImp
import com.welie.blessed.BluetoothPeripheral
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class TrainingSoloViewModel(private val trainer: TrainerImp) : ViewModel() {

    private var connectingWith: BluetoothPeripheral? = null

    val isTrainingNotStarted get() = !trainer.isTrainingActive && !trainer.isTrainingPaused
    val isTrainingStarted get() = trainer.isTrainingActive || trainer.isTrainingPaused

    private val _sessionType = MutableStateFlow<SessionType>(SessionType.Unknown)
    val sessionType = _sessionType.asStateFlow()

    fun connect(mac: String): Boolean {
        val sensor = trainer.getPeripheralFromMac(mac)

        return if (sensor != null && !sensor.isUncached) {
            connectingWith = sensor

            viewModelScope.launch {

                if (trainer.connectedSensor != null) {
                    trainer.cancelConnection(trainer.connectedSensor)
                    delay(3000)
                }

                trainer.connectSensorWithTimeout(sensor, false)
            }

            true
        } else {
            false
        }
    }

    fun cancelConnection() {
        if (connectingWith != null) {
            trainer.cancelConnection(connectingWith)
        }
    }

    fun initTrainingType(trainingType: RMTrainingType) {
        val stepsType = trainer.setTrainingType(trainingType)

        if (stepsType != StepsType.NONE) {
            trainer.enableStepsReading(true)
            _sessionType.tryEmit(SessionType.WithSteps(trainingType, stepsType))
        } else {
            trainer.enableStepsReading(false)
            _sessionType.tryEmit(SessionType.Classic(trainingType))
        }
    }

    fun getStepsType(): StepsType {
        return if (_sessionType.value is SessionType.WithSteps) {
            (_sessionType.value as SessionType.WithSteps).stepsType
        } else {
            StepsType.NONE
        }
    }

    fun startTraining() {
        trainer.startTraining()
    }

    fun pauseTraining() {
        trainer.pauseTraining()
    }

    fun resumeTraining() {
        trainer.resumeTraining()
    }

    fun cancelTraining() {
        cancelConnection()
        trainer.cancelTraining()
    }

    fun finishTraining(): String {
        cancelConnection()
        trainer.finishAndUploadTraining() { rmResponse, result ->
            if (rmResponse.isSuccess) {
                Timber.i("Training uploaded summaries and uuid: ${result.trainingUUID} are available")
            } else {
                Timber.i("Training not uploaded only summaries are available")
            }
        }

        return trainer.startTime
    }

    fun releaseTrainingResources() {
        cancelConnection()
        trainer.pauseTraining()
        trainer.onDestroyImp()
        trainer.onDestroy()
    }
}
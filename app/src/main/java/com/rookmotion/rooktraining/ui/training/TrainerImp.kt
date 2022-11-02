package com.rookmotion.rooktraining.ui.training

import android.content.Context
import com.rookmotion.app.sdk.persistence.entities.training.HeartRateDerivedData
import com.rookmotion.app.sdk.rmsensor.BluetoothAdapterState
import com.rookmotion.app.sdk.rmsensor.RMBandContact
import com.rookmotion.app.sdk.rmsensor.SensorConnectionState
import com.rookmotion.app.sdk.rmtrainer.RMTrainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrainerImp(context: Context, private val scope: CoroutineScope) : RMTrainer(context) {

    private val _adapterState = MutableStateFlow(BluetoothAdapterState.NONE)
    val adapterState = _adapterState.asStateFlow()

    private val _connectionState = MutableStateFlow<SensorConnectionState>(SensorConnectionState.None)
    val connectionState = _connectionState.asStateFlow()

    private val _time = MutableStateFlow(0F)
    val time = _time.asStateFlow()

    private val _heartRate = MutableStateFlow(HeartRateDerivedData())
    val heartRate = _heartRate.asStateFlow()

    private val _hrValid = MutableStateFlow(true)
    val hrValid = _hrValid.asStateFlow()

    private val _steps = MutableStateFlow(0L)
    val steps = _steps.asStateFlow()

    private val _batteryLevel = MutableStateFlow(100)
    val batteryLevel = _batteryLevel.asStateFlow()

    private val _bandContactState = MutableStateFlow(RMBandContact.UNKNOWN)
    val bandContactState = _bandContactState.asStateFlow()

    override fun onAdapterStateChanged(state: BluetoothAdapterState?) {
        scope.launch { state?.let { _adapterState.emit(it) } }
    }

    override fun onConnectionStateChanged(state: SensorConnectionState?) {
        scope.launch { state?.let { _connectionState.emit(it) } }
    }

    override fun onTimeReceived(sessionSeconds: Float) {
        scope.launch { _time.emit(sessionSeconds) }
    }

    override fun onHrReceived(hrData: HeartRateDerivedData?) {
        scope.launch { hrData?.let { _heartRate.emit(it) } }
    }

    override fun onHrValidated(isValid: Boolean) {
        scope.launch { _hrValid.emit(isValid) }
    }

    override fun onStepReceived(currentSteps: Long, cadence: Float) {
        scope.launch { _steps.emit(currentSteps) }
    }

    override fun onBatteryLevelReceived(level: Int) {
        scope.launch { _batteryLevel.emit(level) }
    }

    override fun onBandContactReceived(bandContact: RMBandContact?) {
        scope.launch { bandContact?.let { _bandContactState.emit(it) } }
    }

    fun onDestroyImp() {
        scope.cancel()
    }
}
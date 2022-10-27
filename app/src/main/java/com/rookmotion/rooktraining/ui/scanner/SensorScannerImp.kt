package com.rookmotion.rooktraining.ui.scanner

import android.bluetooth.le.ScanResult
import android.content.Context
import com.rookmotion.app.sdk.rmsensor.BluetoothAdapterState
import com.rookmotion.app.sdk.rmsensor.RMSensorScanner
import com.rookmotion.app.sdk.rmsensor.RMSensorScannerListener
import com.welie.blessed.BluetoothPeripheral
import com.welie.blessed.HciStatus
import com.welie.blessed.ScanFailure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class SensorScannerImp(
    context: Context,
    private val scope: CoroutineScope
) : RMSensorScannerListener {

    val scanner by lazy { RMSensorScanner(context, this) }

    private val discovered = mutableMapOf<String, BluetoothPeripheral>()

    private var connectedMac = ""

    private val _adapterState = MutableStateFlow(BluetoothAdapterState.NONE)
    val adapterState = _adapterState.asStateFlow()

    private val _scannerError = MutableStateFlow<ScanFailure?>(null)
    val scannerError get() = _scannerError.asStateFlow()

    private val _discoveredSensors = MutableStateFlow<List<BluetoothPeripheral>>(emptyList())
    val discoveredSensors get() = _discoveredSensors.asStateFlow()

    fun setConnectedMac(mac: String) {
        connectedMac = mac
    }

    fun resetScannerError() {
        scope.launch { _scannerError.emit(null) }
    }

    override fun adapterStateChanged(state: BluetoothAdapterState) {
        scope.launch { _adapterState.emit(state) }
    }

    override fun newSensor(sensor: BluetoothPeripheral, scanResult: ScanResult) {
        if (connectedMac != sensor.address && !discovered.contains(sensor.address)) {
            discovered[sensor.address] = sensor
            _discoveredSensors.value = discovered.values.toList()
        }
    }

    override fun newSensorAdded(sensors: MutableList<BluetoothPeripheral>) {
        Timber.i("newSensorAdded: $sensors")
    }

    override fun scanFailed(scanFailure: ScanFailure) {
        scope.launch { _scannerError.emit(scanFailure) }
    }

    fun onDestroy() {
        scope.cancel()
    }
}
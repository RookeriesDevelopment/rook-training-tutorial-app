package com.rookmotion.rooktraining.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rookmotion.app.sdk.persistence.entities.sensor.RMSensor
import com.rookmotion.rooktraining.data.repository.SensorRepository
import com.rookmotion.rooktraining.ui.scanner.SensorScannerImp
import com.welie.blessed.BluetoothPeripheral
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SensorScannerViewModel(
    val scannerImp: SensorScannerImp,
    private val sensorRepository: SensorRepository
) : ViewModel() {

    private val _connectedSensorState =
        MutableLiveData<ConnectedSensorState>(ConnectedSensorState.None)
    val connectedSensorState: LiveData<ConnectedSensorState> get() = _connectedSensorState

    private val _linkSensorState = MutableLiveData<LinkSensorState>()
    val linkSensorState: LiveData<LinkSensorState> get() = _linkSensorState

    init {
        viewModelScope.launch {
            delay(3000)

            startScan()
        }
    }

    fun setConnected(mac: String?, name: String) {
        if (mac != null) {
            scannerImp.setConnectedMac(mac)

            _connectedSensorState.value = ConnectedSensorState.Connected(mac, name)
        } else {
            _connectedSensorState.value = ConnectedSensorState.None
        }
    }

    fun startScan(fromError: Boolean = false) {
        if (fromError) {
            scannerImp.resetScannerError()
        }

        if (scannerImp.scanner.isBluetoothEnabled && !scannerImp.scanner.isDiscovering) {
            scannerImp.scanner.startDiscovery()
        }
    }

    fun stopScan() {
        if (scannerImp.scanner.isBluetoothEnabled && scannerImp.scanner.isDiscovering) {
            scannerImp.scanner.stopDiscovery()
        }
    }

    fun linkToSensor(peripheral: BluetoothPeripheral) {
        _linkSensorState.value = LinkSensorState(linking = true)

        sensorRepository.saveSensor(
            peripheral = peripheral,
            onSuccess = { saveLastUsedSensor(it) },
            onError = {
                _linkSensorState.value = LinkSensorState(
                    success = false,
                    mac = peripheral.address,
                    error = it
                )
            }
        )
    }

    private fun saveLastUsedSensor(rmSensor: RMSensor) {
        sensorRepository.saveLastUsedSensor(
            rmSensor = rmSensor,
            onSuccess = {
                _linkSensorState.value = LinkSensorState(success = true, mac = rmSensor.sensorMac)
            },
            onError = {
                _linkSensorState.value = LinkSensorState(
                    success = false,
                    mac = rmSensor.sensorMac,
                    error = "Database error"
                )
            }
        )
    }

    fun releaseScannerResources() {
        scannerImp.onDestroy()
        scannerImp.scanner.onDestroy()
    }

    sealed class ConnectedSensorState {
        object None : ConnectedSensorState()
        class Connected(val mac: String, val name: String) : ConnectedSensorState()
    }

    data class LinkSensorState(
        val linking: Boolean = false,
        val success: Boolean = false,
        val mac: String? = null,
        val error: String? = null
    )
}
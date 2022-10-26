package com.rookmotion.rooktraining.data.repository

import com.rookmotion.app.sdk.RM
import com.rookmotion.app.sdk.persistence.entities.sensor.RMSensor
import com.welie.blessed.BluetoothPeripheral

class SensorRepository(private val rm: RM) {

    fun getSensors(
        onSuccess: (List<RMSensor>) -> Unit,
        onError: (String) -> Unit
    ) {
        rm.getSensors { rmResponse, result ->
            if (rmResponse.isSuccess && result != null) {
                onSuccess(result)
            } else {
                onError(rmResponse.message)
            }
        }
    }

    fun saveSensor(
        peripheral: BluetoothPeripheral,
        onSuccess: (RMSensor) -> Unit,
        onError: (String) -> Unit
    ) {
        rm.saveSensorFromBluetoothPeripheral(peripheral) { rmResponse, result ->
            if (rmResponse.isSuccess && result != null) {
                onSuccess(result)
            } else {
                onError(rmResponse.message)
            }
        }
    }

    fun saveLastUsedSensor(
        rmSensor: RMSensor,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        rm.updateLastUsedSensorInDatabase(rmSensor) {
            if (it.isSuccess) {
                onSuccess()
            } else {
                onError(it.message)
            }
        }
    }

    fun deleteSensor(sensor: RMSensor, onFinish: (Boolean) -> Unit) {
        rm.deleteSensor(sensor) { onFinish(it.isSuccess) }
    }
}
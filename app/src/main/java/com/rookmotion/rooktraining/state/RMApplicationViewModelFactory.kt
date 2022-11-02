package com.rookmotion.rooktraining.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rookmotion.rooktraining.RookTrainingApplication
import com.rookmotion.rooktraining.ui.scanner.SensorScannerImp
import com.rookmotion.rooktraining.ui.training.TrainerImp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Suppress("UNCHECKED_CAST")
class RMApplicationViewModelFactory(
    private val application: RookTrainingApplication,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(SensorScannerViewModel::class.java)) {
            return SensorScannerViewModel(
                scannerImp = SensorScannerImp(
                    context = application.applicationContext,
                    scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
                ),
                sensorRepository = application.rmServiceLocator.sensorRepository
            ) as T
        }

        if (modelClass.isAssignableFrom(TrainingSoloViewModel::class.java)) {
            return TrainingSoloViewModel(
                trainer = TrainerImp(
                    context = application.applicationContext,
                    scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
                )
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
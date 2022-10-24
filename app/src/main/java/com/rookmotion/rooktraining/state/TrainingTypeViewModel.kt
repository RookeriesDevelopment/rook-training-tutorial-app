package com.rookmotion.rooktraining.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rookmotion.app.sdk.persistence.entities.training.RMTrainingType
import com.rookmotion.rooktraining.data.repository.TrainingTypeRepository

class TrainingTypeViewModel(
    private val trainingTypeRepository: TrainingTypeRepository
) : ViewModel() {

    private val _trainingTypeState = MutableLiveData<TrainingTypeState>()
    val trainingTypeState: LiveData<TrainingTypeState> get() = _trainingTypeState

    fun getTrainingTypes() {
        _trainingTypeState.value = TrainingTypeState.Loading

        trainingTypeRepository.getTrainingTypes(
            onSuccess = {
                if (it.isEmpty()) {
                    _trainingTypeState.value = TrainingTypeState.NoDataAvailable
                } else {
                    _trainingTypeState.value = TrainingTypeState.DataAvailable(it)
                }
            },
            onError = { _trainingTypeState.value = TrainingTypeState.Error(it) },
        )
    }

    sealed class TrainingTypeState {
        object Loading : TrainingTypeState()
        object NoDataAvailable : TrainingTypeState()
        class DataAvailable(val data: List<RMTrainingType>) : TrainingTypeState()
        class Error(val message: String) : TrainingTypeState()
    }
}
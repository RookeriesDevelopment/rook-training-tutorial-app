package com.rookmotion.rooktraining.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rookmotion.app.sdk.persistence.entities.training.HeartRateDerivedData
import com.rookmotion.app.sdk.persistence.entities.training.RMSummary
import com.rookmotion.app.sdk.persistence.entities.training.RMTrainingModel
import com.rookmotion.app.sdk.persistence.entities.training.StepsDerivedData
import com.rookmotion.rooktraining.data.repository.TrainingSummaryRepository

class TrainingSummaryViewModel(
    private val trainingSummaryRepository: TrainingSummaryRepository
) : ViewModel() {

    private val _trainingSummaryState = MutableLiveData<TrainingSummaryState>()
    val trainingSummaryState: LiveData<TrainingSummaryState> get() = _trainingSummaryState

    fun getTraining(start: String) {
        _trainingSummaryState.value = TrainingSummaryState.Loading

        trainingSummaryRepository.getTrainingInformation(
            start = start,
            onSuccess = { getSummaries(start, it) },
            onError = { _trainingSummaryState.value = TrainingSummaryState.Error(it) }
        )
    }

    private fun getSummaries(start: String, training: RMTrainingModel) {
        trainingSummaryRepository.getSummaries(
            start = start,
            onSuccess = { getHeartRateRecords(start, training, it) },
            onError = { _trainingSummaryState.value = TrainingSummaryState.Error(it) }
        )
    }

    private fun getHeartRateRecords(start: String, training: RMTrainingModel, summary: RMSummary) {
        trainingSummaryRepository.getHeartRateRecords(
            start = start,
            onSuccess = { getStepsRecords(start, training, summary, it) },
            onError = { _trainingSummaryState.value = TrainingSummaryState.Error(it) }
        )
    }

    private fun getStepsRecords(
        start: String,
        training: RMTrainingModel,
        summary: RMSummary,
        heartRateRecords: List<HeartRateDerivedData>,

        ) {
        trainingSummaryRepository.getStepsRecords(
            start = start,
            onSuccess = {
                _trainingSummaryState.value = TrainingSummaryState.Success(
                    training = training,
                    summary = summary,
                    heartRateRecords = heartRateRecords,
                    stepsRecords = it,
                )
            },
            onError = { _trainingSummaryState.value = TrainingSummaryState.Error(it) }
        )
    }

    fun resetTrainingSummaryState() {
        _trainingSummaryState.value = TrainingSummaryState.None
    }

    sealed class TrainingSummaryState {
        object None : TrainingSummaryState()
        object Loading : TrainingSummaryState()
        class Error(val message: String) : TrainingSummaryState()
        class Success(
            val training: RMTrainingModel,
            val summary: RMSummary,
            val heartRateRecords: List<HeartRateDerivedData> = emptyList(),
            val stepsRecords: List<StepsDerivedData> = emptyList()
        ) : TrainingSummaryState()
    }
}
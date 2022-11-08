package com.rookmotion.rooktraining.data.repository

import com.rookmotion.app.sdk.RM
import com.rookmotion.app.sdk.persistence.entities.training.HeartRateDerivedData
import com.rookmotion.app.sdk.persistence.entities.training.RMSummary
import com.rookmotion.app.sdk.persistence.entities.training.RMTrainingModel
import com.rookmotion.app.sdk.persistence.entities.training.StepsDerivedData

class TrainingSummaryRepository(private val rm: RM) {

    fun getTrainingInformation(
        start: String,
        onSuccess: (RMTrainingModel) -> Unit,
        onError: (String) -> Unit
    ) {
        rm.getTrainingInformationFromDatabase(start) { rmResponse, rmTraining ->
            if (rmResponse.isSuccess && rmTraining != null) {
                onSuccess(rmTraining)
            } else {
                onError(rmResponse.message)
            }
        }
    }

    fun getSummaries(start: String, onSuccess: (RMSummary) -> Unit, onError: (String) -> Unit) {
        rm.getSummariesFromDatabase(start) { rmResponse, rmSummary ->
            if (rmResponse.isSuccess && rmSummary != null) {
                onSuccess(rmSummary)
            } else {
                onError(rmResponse.message)
            }
        }
    }

    fun getHeartRateRecords(
        start: String,
        onSuccess: (List<HeartRateDerivedData>) -> Unit,
        onError: (String) -> Unit
    ) {
        rm.getHeartRateRecordsFromDatabase(start) { rmResponse, records ->
            if (rmResponse.isSuccess && records != null) {
                onSuccess(records)
            } else {
                onError(rmResponse.message)
            }
        }
    }

    fun getStepsRecords(
        start: String,
        onSuccess: (List<StepsDerivedData>) -> Unit,
        onError: (String) -> Unit
    ) {
        rm.getStepsRecordsFromDatabase(start) { rmResponse, records ->
            if (rmResponse.isSuccess && records != null) {
                onSuccess(records)
            } else {
                onError(rmResponse.message)
            }
        }
    }
}
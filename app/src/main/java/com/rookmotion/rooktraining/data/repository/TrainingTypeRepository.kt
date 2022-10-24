package com.rookmotion.rooktraining.data.repository

import com.rookmotion.app.sdk.RM
import com.rookmotion.app.sdk.persistence.entities.training.RMTrainingType

class TrainingTypeRepository(private val rm: RM) {

    fun getTrainingTypes(
        onSuccess: (List<RMTrainingType>) -> Unit,
        onError: (String) -> Unit
    ) {
        rm.getTrainingTypes { rmResponse, result ->
            if (rmResponse.isSuccess && result != null) {
                onSuccess(result)
            } else {
                onError(rmResponse.message)
            }
        }
    }
}
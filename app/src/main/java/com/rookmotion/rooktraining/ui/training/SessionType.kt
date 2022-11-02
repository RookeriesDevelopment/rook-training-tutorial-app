package com.rookmotion.rooktraining.ui.training

import com.rookmotion.app.sdk.persistence.entities.training.RMTrainingType
import com.rookmotion.kotlin.sdk.domain.enums.StepsType

sealed class SessionType {
    object Unknown : SessionType()
    class Classic(val trainingType: RMTrainingType) : SessionType()
    class WithSteps(val trainingType: RMTrainingType, val stepsType: StepsType) : SessionType()
}
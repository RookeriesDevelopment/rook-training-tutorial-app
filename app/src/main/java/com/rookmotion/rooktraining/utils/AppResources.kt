package com.rookmotion.rooktraining.utils

import android.content.Context
import com.rookmotion.kotlin.sdk.domain.enums.StepsType
import com.rookmotion.rooktraining.R

object AppResources {

    fun suffixOfStepsType(context: Context, stepsType: StepsType): String {
        return when (stepsType) {
            StepsType.TRAMPOLINE, StepsType.BOOTS, StepsType.JUMPS -> context.getString(R.string.jumps)
            StepsType.STEPS, StepsType.RUN, StepsType.ROPE -> context.getString(R.string.steps)
            StepsType.NONE -> ""
        }
    }
}
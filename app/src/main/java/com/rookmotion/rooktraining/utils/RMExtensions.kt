package com.rookmotion.rooktraining.utils

import android.app.Activity
import com.rookmotion.rooktraining.RookTrainingApplication
import com.rookmotion.rooktraining.rm.RMServiceLocator

val Activity.rmLocator: RMServiceLocator get() = (application as RookTrainingApplication).rmServiceLocator
package com.rookmotion.rooktraining.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.rookmotion.rooktraining.RookTrainingApplication
import com.rookmotion.rooktraining.rm.RMServiceLocator

val Activity.rmLocator: RMServiceLocator get() = (application as RookTrainingApplication).rmServiceLocator

fun Context.toastShort(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.toastLong(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}
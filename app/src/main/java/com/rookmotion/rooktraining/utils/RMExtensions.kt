package com.rookmotion.rooktraining.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.rookmotion.rooktraining.RookTrainingApplication
import com.rookmotion.rooktraining.rm.RMServiceLocator

val Activity.rmLocator: RMServiceLocator get() = (application as RookTrainingApplication).rmServiceLocator

fun Context.toastShort(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.toastLong(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun View.snackLong(message: String, action: String, onClick: () -> Unit) {
    Snackbar.make(
        this,
        message,
        Snackbar.LENGTH_LONG
    ).setAction(action) { onClick() }.show()
}

fun Activity.setStatusBarColor(colorResourceId: Int) {
    window?.statusBarColor = ContextCompat.getColor(this, colorResourceId)
}
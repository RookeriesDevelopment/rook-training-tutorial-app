package com.rookmotion.rooktraining

import android.app.Application
import com.rookmotion.rooktraining.rm.RMServiceLocator
import timber.log.Timber

class RookTrainingApplication : Application() {
    val rmServiceLocator by lazy { RMServiceLocator(this) }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
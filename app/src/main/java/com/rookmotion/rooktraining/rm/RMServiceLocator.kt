package com.rookmotion.rooktraining.rm

import android.content.Context
import com.rookmotion.app.sdk.RM
import com.rookmotion.app.sdk.RMSettings
import com.rookmotion.kotlin.sdk.RookMotion
import com.rookmotion.kotlin.sdk.data.remote.ApiLogType
import com.rookmotion.kotlin.sdk.data.remote.RMApiSettings
import com.rookmotion.rooktraining.BuildConfig

class RMServiceLocator(context: Context) {

    private val settings = RMSettings.getInstance().apply {
        addAuth(BuildConfig.AUTH_TOKEN, BuildConfig.LEVEL_TOKEN)
        enableCore(BuildConfig.CORE_URL)
    }

    private val apiSettings = run {
        if (BuildConfig.DEBUG) {
            RMApiSettings(ApiLogType.ADVANCED, 60000)
        } else {
            RMApiSettings(ApiLogType.NONE, 60000)
        }
    }

    val rm: RM by lazy { RM.getInstance(context, settings, apiSettings) }
    val rookMotion: RookMotion by lazy { RookMotion.getInstance(context, settings, apiSettings) }
}
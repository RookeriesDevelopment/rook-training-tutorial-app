package com.rookmotion.rooktraining.ui.scanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rookmotion.rooktraining.databinding.ActivitySensorScannerBinding

class SensorScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySensorScannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
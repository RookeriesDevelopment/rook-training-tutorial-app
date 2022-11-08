package com.rookmotion.rooktraining.ui.training.summary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rookmotion.rooktraining.databinding.ActivityTrainingSummaryBinding

class TrainingSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrainingSummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
package com.rookmotion.rooktraining.ui.training.solo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rookmotion.rooktraining.databinding.ActivityTrainingSoloBinding

class TrainingSoloActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrainingSoloBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingSoloBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
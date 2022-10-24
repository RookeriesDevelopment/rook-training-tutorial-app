package com.rookmotion.rooktraining.ui.trainingtype

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rookmotion.rooktraining.databinding.ActivityTrainingTypeBinding

class TrainingTypeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrainingTypeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}
package com.rookmotion.rooktraining.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rookmotion.rooktraining.rm.RMServiceLocator

@Suppress("UNCHECKED_CAST")
class RMViewModelFactory(
    private val rmServiceLocator: RMServiceLocator,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(rmServiceLocator.authRepository) as T
        }

        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(rmServiceLocator.userRepository) as T
        }

        if (modelClass.isAssignableFrom(TrainingTypeViewModel::class.java)) {
            return TrainingTypeViewModel(rmServiceLocator.trainingTypeRepository) as T
        }

        if (modelClass.isAssignableFrom(TrainingSummaryViewModel::class.java)) {
            return TrainingSummaryViewModel(rmServiceLocator.trainingSummaryRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
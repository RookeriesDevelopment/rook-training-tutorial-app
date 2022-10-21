package com.rookmotion.rooktraining.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rookmotion.app.sdk.persistence.entities.user.RMUser
import com.rookmotion.rooktraining.data.repository.UserRepository

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<RMUser?>()
    val user: LiveData<RMUser?> get() = _user

    fun getUser() {
        userRepository.getUserFromDatabase(
            onSuccess = { _user.value = it },
            onError = { _user.value = null }
        )
    }

    fun syncIndexes() {
        userRepository.syncIndexes()
    }
}
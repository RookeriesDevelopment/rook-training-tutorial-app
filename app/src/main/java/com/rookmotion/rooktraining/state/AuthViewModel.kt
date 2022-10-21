package com.rookmotion.rooktraining.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rookmotion.app.sdk.persistence.entities.user.RMUser
import com.rookmotion.rooktraining.data.repository.AuthRepository

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> get() = _loginState

    private val _logOutState = MutableLiveData<LogOutState>()
    val logOutState: LiveData<LogOutState> get() = _logOutState

    fun checkUserAuthentication() {
        authRepository.isUserAuthenticated { _isAuthenticated.value = it }
    }

    fun login(email: String) {
        _loginState.value = LoginState.Loading

        authRepository.login(
            email = email,
            onSuccess = { _loginState.value = LoginState.Logged(it) },
            onError = { _loginState.value = LoginState.Error(it) },
        )
    }

    fun logOut() {
        _logOutState.value = LogOutState.LOADING

        authRepository.logOut {
            _logOutState.value = if (it) LogOutState.LOGGED_OUT else LogOutState.ERROR
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.None
    }

    fun resetLogOutState() {
        _logOutState.value = LogOutState.NONE
    }

    sealed class LoginState {
        object None : LoginState()
        object Loading : LoginState()
        class Logged(val rmUser: RMUser) : LoginState()
        class Error(val message: String) : LoginState()
    }

    enum class LogOutState {
        NONE,
        LOADING,
        LOGGED_OUT,
        ERROR,
    }
}
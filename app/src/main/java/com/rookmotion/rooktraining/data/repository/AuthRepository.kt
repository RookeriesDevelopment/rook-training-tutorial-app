package com.rookmotion.rooktraining.data.repository

import com.rookmotion.app.sdk.RM
import com.rookmotion.app.sdk.persistence.entities.user.RMUser
import com.rookmotion.kotlin.sdk.utils.isValid

class AuthRepository(private val rm: RM) {

    fun isUserAuthenticated(onFinish: (Boolean) -> Unit) {
        rm.getUserFromDatabase { rmResponse, rmUser: RMUser? ->
            if (rmResponse.isSuccess && rmUser != null && rmUser.isValid()) {
                onFinish(true)
            } else {
                onFinish(false)
            }
        }
    }

    fun login(email: String, onSuccess: (RMUser) -> Unit, onError: (String) -> Unit) {
        rm.registerUserInApi(email) { registerResponse, userUUID: String? ->
            if (registerResponse.isSuccess && !userUUID.isNullOrBlank()) {
                getUserProfile(userUUID, onSuccess, onError)
            } else {
                onError(registerResponse.message)
            }
        }
    }

    private fun getUserProfile(
        userUUID: String,
        onSuccess: (RMUser) -> Unit,
        onError: (String) -> Unit
    ) {
        rm.getUserFromApi("", userUUID) { getUserResponse, userFromApi: RMUser? ->
            if (getUserResponse.isSuccess && userFromApi != null && userFromApi.isValid()) {
                saveUserProfile(userFromApi, onSuccess, onError)
            } else {
                onError(getUserResponse.message)
            }
        }
    }

    private fun saveUserProfile(
        profile: RMUser,
        onSuccess: (RMUser) -> Unit,
        onError: (String) -> Unit
    ) {
        rm.saveUserInDatabase(profile) { saveUserResponse ->
            if (saveUserResponse.isSuccess) {
                onSuccess(profile)
            } else {
                onError(saveUserResponse.message)
            }
        }
    }

    fun logOut(onFinish: (Boolean) -> Unit) {
        rm.deleteUserFromDB(true) { deleteUserResponse -> // Delete user data
            if (deleteUserResponse.isSuccess) {
                rm.doLogoutToDatabase() { deleteDataResponse -> // Delete all data
                    onFinish(deleteDataResponse.isSuccess)
                }
            } else {
                onFinish(false)
            }
        }
    }
}
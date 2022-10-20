package com.rookmotion.rooktraining.data.repository

import com.rookmotion.app.sdk.RM
import com.rookmotion.app.sdk.persistence.entities.user.RMUser
import com.rookmotion.kotlin.sdk.utils.isValid

class UserRepository(private val rm: RM) {

    fun getUserFromDatabase(onSuccess: (RMUser) -> Unit, onError: (String) -> Unit) {
        rm.getUserFromDatabase { rmResponse, rmUser: RMUser? ->
            if (rmResponse.isSuccess && rmUser != null && rmUser.isValid()) {
                onSuccess(rmUser)
            } else {
                onError(rmResponse.message)
            }
        }
    }
}
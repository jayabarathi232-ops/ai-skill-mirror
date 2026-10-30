package com.simats.aiskillmirror.data.repository

import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.NetworkResult
import com.simats.aiskillmirror.data.network.UserData
import com.simats.aiskillmirror.data.network.UserSession

class AuthRepository {
    suspend fun login(email: String, pass: String): NetworkResult<UserData> {
        val result = ApiClient.login(email, pass)
        if (result.isSuccess && result.data != null) {
            UserSession.update(result.data.id, result.data.name, result.data.email)
        }
        return result
    }

    suspend fun signup(name: String, email: String, pass: String): NetworkResult<UserData> {
        val result = ApiClient.signup(name, email, pass)
        if (result.isSuccess && result.data != null) {
            UserSession.update(result.data.id, result.data.name, result.data.email)
        }
        return result
    }

    suspend fun recoverPassword(email: String): NetworkResult<Boolean> {
        return ApiClient.requestPasswordRecovery(email)
    }

    fun logout() {
        UserSession.update(0, "", "")
    }
}

